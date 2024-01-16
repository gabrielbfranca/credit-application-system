package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*

@SpringBootTest
class CreditResourceTest {
    @Autowired
    lateinit var creditRepository: CreditRepository
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() = creditRepository.deleteAll()
    @AfterEach
    fun tearDown() = creditRepository.deleteAll()
    companion object {
        const val URL: String = "/api/credits"
    }


    @Test
    fun `should create credit`() {
        // given
        val customer = builderCustomerDTO()
        val creditDto: Credit = builderCreditDTO(customer = customer)
        val stringValue = objectMapper.writeValueAsString(creditDto)

        // when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringValue)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Gabriel"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Franca"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("gabriel@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("10000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("77777"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua do gabriel, 123"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andDo(MockMvcResultHandlers.print())


    }
    
    @Test
    fun `should find all credits by customer id`() {
        // given
        val customerId = 1L
        // when
        val creditList: List<Credit> = creditRepository.findAllByCustomerId(customerId)
        //then
        Assertions.assertThat(creditList).isNotEmpty
        Assertions.assertThat(creditList.size).isEqualTo(1)
    }
    
    @Test
    fun `should find by credit code`() {
        //given
        val creditCode = UUID.fromString("aa547c0f-9a6a-451f-8c89-afddce916a29")
        //when
        val fakeCredit: Credit = creditRepository.findByCreditCode(creditCode)!!
        //then
        Assertions.assertThat(fakeCredit).isNotNull
    }
    private fun builderCustomerDTO(
        firstName: String = "Gabriel",
        lastName: String = "Franca",
        email: String = "gabriel@email.com",
        cpf: String = "28475934625",
        income: BigDecimal = BigDecimal.valueOf(10000.0),
        password: String = "123456",
        zipCode: String = "77777",
        street: String = "Rua do gabriel, 123"
    ) : Customer = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street
        )
    )


    private fun builderCreditDTO(
        creditValue : BigDecimal = BigDecimal.valueOf(1000.0),
        dayFirstOfInstallment: LocalDate = LocalDate.of(2023, Month.FEBRUARY, 11),
        numberOfInstallments: Int = 5,
        customer: Customer,
    ) : Credit = Credit(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstOfInstallment,
        numberOfInstallments = numberOfInstallments,
        customer = customer

    )

}