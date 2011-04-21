package org.springframework.data.jdbc.query;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.query.domain.Customer;
import org.springframework.data.jdbc.query.domain.CustomerQ;
import org.springframework.data.jdbc.query.generated.QCustomer;
import org.springframework.data.jdbc.query.QueryDslJdbcTemplate;
import org.springframework.data.jdbc.query.SqlDeleteCallback;
import org.springframework.data.jdbc.query.SqlInsertCallback;
import org.springframework.data.jdbc.query.SqlUpdateCallback;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.types.QBean;

@Transactional
@Repository
public class QueryDslCustomerDao implements CustomerDao {

    private final QCustomer qCustomer = QCustomer.customer;

    private QueryDslJdbcTemplate template;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.template = new QueryDslJdbcTemplate(dataSource);
	}
	
	public Customer findById(Long id) {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer)
			.where(qCustomer.id.eq(id));
		return template.queryForObject(sqlQuery, 
			BeanPropertyRowMapper.newInstance(Customer.class), 
			qCustomer.all());
	}
	
	public CustomerQ findByIdQ(Long id) {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer)
			.where(qCustomer.id.eq(id));
		return template.queryForObject(sqlQuery, 
			new QBean<CustomerQ>(CustomerQ.class, qCustomer.all()));
	}

	public List<Customer> findAll() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer);
		return template.query(sqlQuery, 
			BeanPropertyRowMapper.newInstance(Customer.class), 
			qCustomer.all());
	}

	public List<CustomerQ> findAllQ() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer);
		return template.query(sqlQuery,
				new QBean<CustomerQ>(CustomerQ.class, qCustomer.all()));
	}

	public void add(final Customer customer) {
		template.insert(qCustomer, new SqlInsertCallback() {
			@Override
			public long doInSqlInsertClause(SQLInsertClause sqlInsertClause) {
				return sqlInsertClause.columns(qCustomer.firstName, qCustomer.lastName)
					.values(customer.getFirstName(), customer.getLastName()).execute();
			}
		});
	}

	@Override
	public void save(final Customer customer) {
		template.update(qCustomer, new SqlUpdateCallback() {
			@Override
			public long doInSqlUpdateClause(SQLUpdateClause sqlUpdateClause) {
				return sqlUpdateClause.where(qCustomer.id.eq(customer.getId()))
					.set(qCustomer.firstName, customer.getFirstName())
					.set(qCustomer.lastName, customer.getLastName())
					.execute();
			}
		});
	}

	@Override
	public void delete(final Customer customer) {
		template.delete(qCustomer, new SqlDeleteCallback() {
			@Override
			public long doInSqlDeleteClause(SQLDeleteClause sqlDeleteClause) {
				return sqlDeleteClause.where(qCustomer.id.eq(customer.getId()))
					.execute();
			}
		});
	}

	
}