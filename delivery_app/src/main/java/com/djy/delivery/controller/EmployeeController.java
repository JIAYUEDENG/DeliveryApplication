package com.djy.delivery.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.djy.delivery.common.R;
import com.djy.delivery.entity.Employee;
import com.djy.delivery.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequestMapping("/employee")
@RestController
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         * 1. 将页面提交的密码password进行md5加密处理
         * 2. 根据页面提交的用户名username查询数据库
         * 3. 如果没有查询到则返回登录失败结果
         * 4. 密码比对，如果不一致则返回登录失败结果
         * 5. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
         * 6. 登录成功，将员工id存入session并返回登陆成功结果
         */

        //1. 将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2. 根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3. 如果没有查询到则返回登录失败结果
        if (emp == null) return R.error("登录失败");

        //4. 密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) return R.error("登录失败");

        //5. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) return R.error("禁用");

        //6. 登录成功，将员工id存入session并返回登陆成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前登录员工的ID
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
}
