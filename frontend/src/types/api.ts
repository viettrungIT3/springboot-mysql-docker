// API Types
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token?: string;
  username?: string;
  authorities?: Array<{ authority: string }>;
  message?: string;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  quantityInStock: number;
  slug?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Customer {
  id: number;
  name: string;
  contactInfo: string;
  slug?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Order {
  id: number;
  customerId: number;
  orderDate: string;
  totalAmount: number;
  customer?: Customer;
  items?: OrderItem[];
  createdAt?: string;
  updatedAt?: string;
}

export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  quantity: number;
  price: number;
  product?: Product;
  createdAt?: string;
  updatedAt?: string;
}

export interface Supplier {
  id: number;
  name: string;
  contactInfo: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface StockEntry {
  id: number;
  productId: number;
  supplierId: number;
  quantity: number;
  entryDate: string;
  entryType: 'IN' | 'OUT';
  product?: Product;
  supplier?: Supplier;
  createdAt?: string;
  updatedAt?: string;
}

export interface Administrator {
  id: number;
  username: string;
  email: string;
  fullName: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}
