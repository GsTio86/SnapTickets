package me.gt.snaptickets.mapper;

import me.gt.snaptickets.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO users (username, password, name, email, phone, address) " +
            "VALUES (#{username}, #{password}, #{name}, #{email}, #{phone}, #{address})")
    void register(User user);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User getByUsername(String username);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User getByEmail(String email);

    @Select("SELECT * FROM users")
    List<User> getAllUsers();

    @Update("UPDATE users SET password = #{password} WHERE username = #{username}")
    void updatePassword(String username, String password);

    @Update("UPDATE users SET name = #{name}, email = #{email}, phone = #{phone}, address = #{address} WHERE username = #{username}")
    int updateUser(User user);

    @Delete("DELETE FROM users WHERE username = #{username}")
    void deleteUser(String username);
}
