"use client";

import { authService } from "@/services/auth";
import { useAuthStore } from "@/store/auth";
import {
  DashboardOutlined,
  FileTextOutlined,
  InboxOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  ShopOutlined,
  ShoppingOutlined,
  TeamOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { Avatar, Button, Dropdown, Layout, Menu, Typography } from "antd";
import { useRouter } from "next/navigation";
import { useState } from "react";
import AuthGuard from "./AuthGuard";

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

interface DashboardLayoutProps {
  children: React.ReactNode;
}

export default function DashboardLayout({ children }: DashboardLayoutProps) {
  const [collapsed, setCollapsed] = useState(false);
  const router = useRouter();
  const { user, logout } = useAuthStore();

  const handleLogout = () => {
    authService.logout();
    logout();
    router.push("/login");
  };

  const menuItems = [
    {
      key: "dashboard",
      icon: <DashboardOutlined />,
      label: "Tổng quan",
      onClick: () => router.push("/dashboard"),
    },
    {
      key: "products",
      icon: <ShoppingOutlined />,
      label: "Sản phẩm",
      onClick: () => router.push("/dashboard/products"),
    },
    {
      key: "customers",
      icon: <UserOutlined />,
      label: "Khách hàng",
      onClick: () => router.push("/dashboard/customers"),
    },
    {
      key: "suppliers",
      icon: <ShopOutlined />,
      label: "Nhà cung cấp",
      onClick: () => router.push("/dashboard/suppliers"),
    },
    {
      key: "orders",
      icon: <FileTextOutlined />,
      label: "Đơn hàng",
      onClick: () => router.push("/dashboard/orders"),
    },
    {
      key: "stock",
      icon: <InboxOutlined />,
      label: "Kho hàng",
      onClick: () => router.push("/dashboard/stock"),
    },
    {
      key: "administrators",
      icon: <TeamOutlined />,
      label: "Quản trị viên",
      onClick: () => router.push("/dashboard/administrators"),
    },
  ];

  const userMenuItems = [
    {
      key: "profile",
      icon: <UserOutlined />,
      label: "Thông tin cá nhân",
    },
    {
      key: "logout",
      icon: <LogoutOutlined />,
      label: "Đăng xuất",
      onClick: handleLogout,
    },
  ];

  return (
    <AuthGuard>
      <Layout className="min-h-screen">
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div className="h-16 flex items-center justify-center bg-blue-600">
          <Title level={4} className="text-white m-0">
            {collapsed ? "IMS" : "Inventory"}
          </Title>
        </div>
        <Menu
          theme="dark"
          mode="inline"
          defaultSelectedKeys={["dashboard"]}
          items={menuItems}
        />
      </Sider>

      <Layout>
        <Header className="bg-white px-4 flex items-center justify-between shadow-sm">
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            className="text-lg"
          />

          <div className="flex items-center gap-4">
            <span className="text-gray-600">Xin chào, {user?.fullName}</span>
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Avatar icon={<UserOutlined />} />
            </Dropdown>
          </div>
        </Header>

        <Content className="p-6 bg-gray-50">{children}</Content>
      </Layout>
    </Layout>
    </AuthGuard>
  );
}
