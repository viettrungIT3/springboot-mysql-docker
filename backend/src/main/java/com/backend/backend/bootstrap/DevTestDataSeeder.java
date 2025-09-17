package com.backend.backend.bootstrap;

import com.backend.backend.config.SeedProperties;
import com.backend.backend.entity.User;
import com.backend.backend.entity.Customer;
import com.backend.backend.entity.Product;
import com.backend.backend.repository.UserRepository;
import com.backend.backend.repository.CustomerRepository;
import com.backend.backend.repository.ProductRepository;
import com.backend.backend.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Random;

@Component
@Profile({"dev", "test"})
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class DevTestDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevTestDataSeeder.class);

    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final UserRepository userRepository;
    private final SeedProperties props;

    @Override
    @Transactional
    public void run(String... args) {
        // Dùng seed cố định cho reproducibility trong test
        Faker faker = new Faker(new Locale("en"), new Random(12345));

        seedProducts(faker, props.getProducts());
        seedCustomers(faker, props.getCustomers());
        seedAdministrators();

        log.info("Seeding completed: products={}, customers={}, administrators={}",
                productRepo.count(), customerRepo.count(), userRepository.count());
    }

    private void seedProducts(Faker faker, int count) {
        int created = 0;
        for (int i = 0; i < count; i++) {
            String name = faker.commerce().productName(); // ví dụ: "Practical Wooden Chair"
            if (productRepo.findByName(name).isPresent()) continue;

            BigDecimal price = randomPrice(faker);
            Integer stock = faker.number().numberBetween(0, 50);
            String description = faker.lorem().sentence(10);

            Product p = Product.builder()
                    .name(name)
                    .slug(generateUniqueSlug(name, productRepo))
                    .description(description)
                    .price(price)
                    .quantityInStock(stock)
                    .build();

            productRepo.save(p);
            created++;
        }
        log.info("Seeded products: {}", created);
    }

    private void seedCustomers(Faker faker, int count) {
        int created = 0;
        for (int i = 0; i < count; i++) {
            String name = faker.name().fullName();
            if (customerRepo.findByName(name).isPresent()) continue;

            String contactInfo = String.format("Email: %s, Phone: %s", 
                    faker.internet().emailAddress(), 
                    faker.phoneNumber().cellPhone());

            Customer c = Customer.builder()
                    .name(name)
                    .slug(generateUniqueSlug(name, customerRepo))
                    .contactInfo(contactInfo)
                    .build();

            customerRepo.save(c);
            created++;
        }
        log.info("Seeded customers: {}", created);
    }

    private BigDecimal randomPrice(Faker faker) {
        // 5.00 -> 2,000.00
        double raw = faker.number().randomDouble(2, 5, 2000);
        return BigDecimal.valueOf(raw).setScale(2, RoundingMode.HALF_UP);
    }

    private String generateUniqueSlug(String name, ProductRepository repo) {
        String baseSlug = SlugUtil.toSlug(name);
        String slug = baseSlug;
        int counter = 1;
        while (repo.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }

    private String generateUniqueSlug(String name, CustomerRepository repo) {
        String baseSlug = SlugUtil.toSlug(name);
        String slug = baseSlug;
        int counter = 1;
        while (repo.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }

    private void seedAdministrators() {
        // Create default admin account
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .fullName("System Administrator")
                    .build();
            admin.setPassword("admin123"); // This will hash the password
            
            userRepository.save(admin);
            log.info("Created default administrator: admin/admin123");
        }
    }
}
