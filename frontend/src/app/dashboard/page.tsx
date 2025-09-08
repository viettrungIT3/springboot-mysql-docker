"use client";

import DashboardLayout from "@/components/DashboardLayout";
import {
  ArrowDownOutlined,
  ArrowUpOutlined,
  FileTextOutlined,
  InboxOutlined,
  ShoppingOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { Card, Col, Row, Statistic, Table, Tag, Typography } from "antd";

const { Title } = Typography;

export default function DashboardPage() {
  // Mock data - sẽ được thay thế bằng API calls
  const stats = [
    {
      title: "Tổng sản phẩm",
      value: 156,
      icon: <ShoppingOutlined />,
      color: "#1890ff",
      change: "+12%",
      trend: "up",
    },
    {
      title: "Khách hàng",
      value: 89,
      icon: <UserOutlined />,
      color: "#52c41a",
      change: "+8%",
      trend: "up",
    },
    {
      title: "Đơn hàng",
      value: 234,
      icon: <FileTextOutlined />,
      color: "#fa8c16",
      change: "+15%",
      trend: "up",
    },
    {
      title: "Tồn kho",
      value: 1234,
      icon: <InboxOutlined />,
      color: "#722ed1",
      change: "-3%",
      trend: "down",
    },
  ];

  const recentOrders = [
    {
      key: "1",
      id: "ORD-001",
      customer: "Nguyễn Văn A",
      amount: 1250000,
      status: "completed",
      date: "2024-01-15",
    },
    {
      key: "2",
      id: "ORD-002",
      customer: "Trần Thị B",
      amount: 890000,
      status: "pending",
      date: "2024-01-14",
    },
    {
      key: "3",
      id: "ORD-003",
      customer: "Lê Văn C",
      amount: 2100000,
      status: "processing",
      date: "2024-01-13",
    },
  ];

  const columns = [
    {
      title: "Mã đơn hàng",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "Khách hàng",
      dataIndex: "customer",
      key: "customer",
    },
    {
      title: "Số tiền",
      dataIndex: "amount",
      key: "amount",
      render: (amount: number) => `${amount.toLocaleString("vi-VN")} VNĐ`,
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      key: "status",
      render: (status: string) => {
        const color =
          status === "completed"
            ? "green"
            : status === "pending"
            ? "orange"
            : "blue";
        const text =
          status === "completed"
            ? "Hoàn thành"
            : status === "pending"
            ? "Chờ xử lý"
            : "Đang xử lý";
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: "Ngày tạo",
      dataIndex: "date",
      key: "date",
    },
  ];

  return (
    <DashboardLayout>
      <div>
        <Title level={2} className="mb-6">
          Tổng quan hệ thống
        </Title>

        {/* Statistics Cards */}
        <Row gutter={[16, 16]} className="mb-6">
          {stats.map((stat, index) => (
            <Col xs={24} sm={12} lg={6} key={index}>
              <Card>
                <Statistic
                  title={stat.title}
                  value={stat.value}
                  prefix={stat.icon}
                  valueStyle={{ color: stat.color }}
                  suffix={
                    <span
                      style={{
                        color: stat.trend === "up" ? "#52c41a" : "#ff4d4f",
                        fontSize: "14px",
                      }}
                    >
                      {stat.trend === "up" ? (
                        <ArrowUpOutlined />
                      ) : (
                        <ArrowDownOutlined />
                      )}
                      {stat.change}
                    </span>
                  }
                />
              </Card>
            </Col>
          ))}
        </Row>

        {/* Recent Orders */}
        <Row gutter={[16, 16]}>
          <Col xs={24} lg={16}>
            <Card
              title="Đơn hàng gần đây"
              extra={<a href="/dashboard/orders">Xem tất cả</a>}
            >
              <Table
                dataSource={recentOrders}
                columns={columns}
                pagination={false}
                size="small"
              />
            </Card>
          </Col>

          <Col xs={24} lg={8}>
            <Card title="Thông báo">
              <div className="space-y-3">
                <div className="p-3 bg-blue-50 rounded">
                  <div className="font-medium text-blue-800">
                    Sản phẩm sắp hết hàng
                  </div>
                  <div className="text-sm text-blue-600">
                    5 sản phẩm cần nhập thêm
                  </div>
                </div>
                <div className="p-3 bg-green-50 rounded">
                  <div className="font-medium text-green-800">Đơn hàng mới</div>
                  <div className="text-sm text-green-600">
                    3 đơn hàng chờ xử lý
                  </div>
                </div>
                <div className="p-3 bg-orange-50 rounded">
                  <div className="font-medium text-orange-800">
                    Cập nhật hệ thống
                  </div>
                  <div className="text-sm text-orange-600">
                    Phiên bản mới đã sẵn sàng
                  </div>
                </div>
              </div>
            </Card>
          </Col>
        </Row>
      </div>
    </DashboardLayout>
  );
}
