-- 建立會員資料表
CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(20) NOT NULL PRIMARY KEY,
  password VARCHAR NOT NULL,
  name VARCHAR(20),
  email VARCHAR(50),
  phone VARCHAR(20),
  address VARCHAR(100),
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- 設定會員資料表的註解
COMMENT ON COLUMN users.username IS '帳號';
COMMENT ON COLUMN users.password IS '密碼';
COMMENT ON COLUMN users.name IS '姓名';
COMMENT ON COLUMN users.email IS '電子郵件';
COMMENT ON COLUMN users.phone IS '手機號碼';
COMMENT ON COLUMN users.address IS '地址';
COMMENT ON COLUMN users.createdAt IS '建立時間';


-- 建立票券資料表
CREATE TABLE IF NOT EXISTS tickets (
    ticketId VARCHAR PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    image BYTEA,
    price INT NOT NULL,
    stock INT NOT NULL,
    startDate TIMESTAMP,
    endDate TIMESTAMP,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 設定票券資料表的註解
COMMENT ON COLUMN tickets.ticketId IS '編號';
COMMENT ON COLUMN tickets.name IS '票券名稱';
COMMENT ON COLUMN tickets.description IS '票券描述';
COMMENT ON COLUMN tickets.image IS '票券圖片';
COMMENT ON COLUMN tickets.price IS '票券價格';
COMMENT ON COLUMN tickets.stock IS '庫存數量';
COMMENT ON COLUMN tickets.startDate IS '開賣日期';
COMMENT ON COLUMN tickets.endDate IS '結束日期';
COMMENT ON COLUMN tickets.createdAt IS '建立時間';
COMMENT ON COLUMN tickets.updatedAt IS '更新時間';


-- 建立訂單資料表
CREATE TABLE IF NOT EXISTS orders (
    orderId VARCHAR PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    ticketId VARCHAR NOT NULL,
    orderStatus VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    totalPrice INT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 設定訂單資料表的註解
COMMENT ON COLUMN orders.orderId IS '訂單編號';
COMMENT ON COLUMN orders.username IS '購買帳號';
COMMENT ON COLUMN orders.ticketId IS '票券編號';
COMMENT ON COLUMN orders.orderStatus IS '訂單狀態';
COMMENT ON COLUMN orders.quantity IS '購買數量';
COMMENT ON COLUMN orders.totalPrice IS '總價';
COMMENT ON COLUMN orders.createdAt IS '建立時間';
COMMENT ON COLUMN orders.updatedAt IS '更新時間';


-- 建立付款資料表
CREATE TABLE IF NOT EXISTS payments (
    paymentId VARCHAR PRIMARY KEY,
    orderId VARCHAR NOT NULL,
    paymentMethod VARCHAR(20) NOT NULL,
    paymentStatus VARCHAR(20) NOT NULL,
    paymentAmount INT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 設定付款資料表的註解
COMMENT ON COLUMN payments.paymentId IS '付款編號';
COMMENT ON COLUMN payments.orderId IS '訂單編號';
COMMENT ON COLUMN payments.paymentMethod IS '付款方式';
COMMENT ON COLUMN payments.paymentStatus IS '付款狀態';
COMMENT ON COLUMN payments.paymentAmount IS '付款金額';
COMMENT ON COLUMN payments.createdAt IS '建立時間';
COMMENT ON COLUMN payments.updatedAt IS '更新時間';


-- 建立票券使用資料表
CREATE TABLE IF NOT EXISTS user_ticket (
    userTicketId VARCHAR PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    orderId VARCHAR NOT NULL,
    ticketId VARCHAR NOT NULL,
    code VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    issuedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usedAt TIMESTAMP,
    expiredAt TIMESTAMP
);


-- 設定票券使用資料表的註解
COMMENT ON COLUMN user_ticket.userTicketId IS '使用者票券編號';
COMMENT ON COLUMN user_ticket.username IS '使用者帳號';
COMMENT ON COLUMN user_ticket.orderId IS '訂單編號';
COMMENT ON COLUMN user_ticket.ticketId IS '票券編號';
COMMENT ON COLUMN user_ticket.code IS '票券代碼';
COMMENT ON COLUMN user_ticket.status IS '使用狀態';
COMMENT ON COLUMN user_ticket.issuedAt IS '獲得時間';
COMMENT ON COLUMN user_ticket.usedAt IS '使用時間';
COMMENT ON COLUMN user_ticket.expiredAt IS '使用期限';


-- 建立管理員資料表
CREATE TABLE IF NOT EXISTS admins (
    username VARCHAR(20) PRIMARY KEY,
    password VARCHAR NOT NULL,
    name VARCHAR(20),
    email VARCHAR(50),
    permission VARCHAR(20),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 設定管理員資料表的註解
COMMENT ON COLUMN admins.username IS '帳號';
COMMENT ON COLUMN admins.password IS '密碼';
COMMENT ON COLUMN admins.name IS '姓名';
COMMENT ON COLUMN admins.email IS '電子郵件';
COMMENT ON COLUMN admins.permission IS '權限';
COMMENT ON COLUMN admins.createdAt IS '建立時間';
