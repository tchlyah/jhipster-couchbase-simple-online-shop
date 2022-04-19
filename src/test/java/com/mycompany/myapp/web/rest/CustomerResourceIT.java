package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.repository.CustomerRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link CustomerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CustomerResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/customers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/customers";

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MockMvc restCustomerMockMvc;

    private Customer customer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createEntity() {
        Customer customer = new Customer()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .telephone(DEFAULT_TELEPHONE);
        return customer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createUpdatedEntity() {
        Customer customer = new Customer()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .telephone(UPDATED_TELEPHONE);
        return customer;
    }

    @BeforeEach
    public void initTest() {
        customerRepository.deleteAll();
        customer = createEntity();
    }

    @Test
    void createCustomer() throws Exception {
        int databaseSizeBeforeCreate = customerRepository.findAll().size();
        // Create the Customer
        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customer)))
            .andExpect(status().isCreated());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeCreate + 1);
        Customer testCustomer = customerList.get(customerList.size() - 1);
        assertThat(testCustomer.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCustomer.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCustomer.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCustomer.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
    }

    @Test
    void createCustomerWithExistingId() throws Exception {
        // Create the Customer with an existing ID
        customer.setId("existing_id");

        int databaseSizeBeforeCreate = customerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customer)))
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCustomers() throws Exception {
        // Initialize the database
        customerRepository.save(customer);

        // Get all the customerList
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)));
    }

    @Test
    void getCustomer() throws Exception {
        // Initialize the database
        customerRepository.save(customer);

        // Get the customer
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL_ID, customer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customer.getId()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE));
    }

    @Test
    void getNonExistingCustomer() throws Exception {
        // Get the customer
        restCustomerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewCustomer() throws Exception {
        // Initialize the database
        customerRepository.save(customer);

        int databaseSizeBeforeUpdate = customerRepository.findAll().size();

        // Update the customer
        Customer updatedCustomer = customerRepository.findById(customer.getId()).get();
        updatedCustomer.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).telephone(UPDATED_TELEPHONE);

        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCustomer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCustomer))
            )
            .andExpect(status().isOk());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
        Customer testCustomer = customerList.get(customerList.size() - 1);
        assertThat(testCustomer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCustomer.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCustomer.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCustomer.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
    }

    @Test
    void putNonExistingCustomer() throws Exception {
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();
        customer.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCustomer() throws Exception {
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();
        customer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCustomer() throws Exception {
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();
        customer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCustomerWithPatch() throws Exception {
        // Initialize the database
        customerRepository.save(customer);

        int databaseSizeBeforeUpdate = customerRepository.findAll().size();

        // Update the customer using partial update
        Customer partialUpdatedCustomer = new Customer();
        partialUpdatedCustomer.setId(customer.getId());

        partialUpdatedCustomer.firstName(UPDATED_FIRST_NAME);

        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomer))
            )
            .andExpect(status().isOk());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
        Customer testCustomer = customerList.get(customerList.size() - 1);
        assertThat(testCustomer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCustomer.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCustomer.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCustomer.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
    }

    @Test
    void fullUpdateCustomerWithPatch() throws Exception {
        // Initialize the database
        customerRepository.save(customer);

        int databaseSizeBeforeUpdate = customerRepository.findAll().size();

        // Update the customer using partial update
        Customer partialUpdatedCustomer = new Customer();
        partialUpdatedCustomer.setId(customer.getId());

        partialUpdatedCustomer.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).telephone(UPDATED_TELEPHONE);

        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomer))
            )
            .andExpect(status().isOk());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
        Customer testCustomer = customerList.get(customerList.size() - 1);
        assertThat(testCustomer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCustomer.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCustomer.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCustomer.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
    }

    @Test
    void patchNonExistingCustomer() throws Exception {
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();
        customer.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCustomer() throws Exception {
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();
        customer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCustomer() throws Exception {
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();
        customer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(customer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Customer in the database
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCustomer() throws Exception {
        // Initialize the database
        customerRepository.save(customer);

        int databaseSizeBeforeDelete = customerRepository.findAll().size();

        // Delete the customer
        restCustomerMockMvc
            .perform(delete(ENTITY_API_URL_ID, customer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    void searchCustomer() throws Exception {
        // Initialize the database
        customerRepository.save(customer);

        // Wait for the customer to be indexed
        TestUtil.retryUntilNotEmpty(() -> customerRepository.search("id:" + customer.getId()));

        // Search the customer
        restCustomerMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + customer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)));
    }
}
