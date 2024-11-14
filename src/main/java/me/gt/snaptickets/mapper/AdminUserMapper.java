package me.gt.snaptickets.mapper;

import me.gt.snaptickets.model.AdminUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminUserMapper {

    @Insert("INSERT INTO admins (username, password, name, email, permission) " +
            "VALUES (#{username}, #{password}, #{name}, #{email}, #{permission})")
    void register(AdminUser user);

    @Select("SELECT * FROM admins WHERE username = #{username}")
    AdminUser getByUsername(String username);

    @Select("SELECT * FROM admins WHERE email = #{email}")
    AdminUser getByEmail(String email);

    @Select("SELECT * FROM admins")
    List<AdminUser> getAll();

    @Update("UPDATE admins SET password = #{password} WHERE username = #{username}")
    void updatePassword(String username, String password);

    @Update("UPDATE admins SET name = #{name}, email = #{email}, permission = #{permission} WHERE username = #{username}")
    int updateUser(AdminUser user);

    @Delete("DELETE FROM admins WHERE username = #{username}")
    void deleteUser(String username);

}
