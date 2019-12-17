package cn.dk.es.repository;

import cn.dk.es.entity.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Test
    public void testForGettingEmployees(){
        Iterable<Employee> employeeIterable = employeeRepository.findAll(Sort.by("age"));
        employeeIterable.forEach(employee-> System.out.println(employee));
    }
}