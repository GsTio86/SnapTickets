# SnapTickets 後端

SnapTickets 是一個票券系統的後端部分，負責處理票務購買、帳號管理、票券查看等功能，並支援多伺服器環境以確保高併發情況下的穩定運行。此後端系統使用 Spring Boot 和 PostgreSQL 進行開發，並整合了 Redis 用於分佈式鎖管理，以及 JWT 用於身份驗證。

---

## 專案功能

- **使用者身份驗證**：支持使用者註冊和使用帳號名或電子郵件登入，並使用 JWT 進行安全驗證。
- **票券管理**：提供購買票券、查詢票券信息、核驗票券有效性的功能，支持高併發情況下的票券庫存管理。
- **Redis分佈式鎖**：使用 Redis 管理分佈式鎖，以防止在多伺服器環境中出現票券超賣的情況。
- **API 認證**：使用基於 JWT 的令牌機制來保護 API 接口，確保只有合法用戶可以訪問。
- **管理面板支持**：提供管理介面 API，方便管理員管理票券庫存、更新票券資料並查看訂單記錄。
- **金流整合**：整合金流API，讓使用者能進行付款作業。

---  

## 使用技術

- **[Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)**：專案使用的 Java 版本。
- **[Spring Boot](https://spring.io/projects/spring-boot)**：作為主要框架，用於建立 RESTful API。
- **[Spring Security](https://spring.io/projects/spring-security)**：控管各API接口的安全驗證設定。
- **[MyBatis](https://mybatis.org/mybatis-3/)**：負責 ORM（物件關聯映射），簡化資料庫操作。
- **[PostgreSQL](https://www.postgresql.org/)**：使用的資料庫類型儲存訂單、付款、票券、帳號等資料。
- **[Redis](https://redis.io/)**：用於管理分佈式鎖，用於時現訂單同一時間超賣的情況。
- **[JWT](https://jwt.io/)**：用於Token驗證，確保使用者和API接口的安全性。
- **[ECPay SDK](https://github.com/ECPay/ECPayAIO_Java)**：與綠界金流整合。
- **[Lombok](https://projectlombok.org/)**：用於簡化Java程式。
- **[Swagger UI](https://swagger.io/tools/swagger-ui/)**：提供 API 文件及互動測試平台。
- **[Thymeleaf](https://www.thymeleaf.org/)**：作為付款完成的回應模板。
- **[Junit 5](https://junit.org/junit5/)**：單元測試。
- **[spring-dotenv](https://github.com/paulschwarz/spring-dotenv)**：管理 Spring Boot 環境變數。  

### **克隆專案**
要開始使用，從 Git 克隆此專案：
```sh
git clone https://github.com/GsTio86/SnapTickets -b backend
