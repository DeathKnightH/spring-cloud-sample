package cn.dk.es.repository;

import cn.dk.es.entity.Employee;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface EmployeeRepository extends ElasticsearchCrudRepository<Employee, Integer> {
}
