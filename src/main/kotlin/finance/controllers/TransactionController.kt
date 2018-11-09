package finance.controllers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import finance.models.Transaction
import finance.services.TransactionService
import finance.pojos.ResultMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.ZonedDateTime
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import java.util.stream.Collectors
import java.util.stream.StreamSupport

//@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@CrossOrigin
//Thymeleaf - RestController is for JSON; Controller is for HTML
@RestController
//@RequestMapping("/transaction")
class TransactionController {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    internal var transactionService: TransactionService? = null

    //localhost:8080/findall?pageNumber=1&pageSize=20
    @GetMapping(path = arrayOf("/findall"))
    //@RequestMapping(value = "/users/get", method = arrayOf(RequestMethod.GET), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    //@RequestMapping(method = RequestMethod.GET, path = "pageable",  produces = MediaType.APPLICATION_JSON_VALUE)
    fun findAllTransactions(@RequestParam pageNumber: Int, @RequestParam pageSize: Int, pageable: Pageable): ResponseEntity<Page<Transaction>> {
        //var  pageable1: Pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "amount")
        var  pageable1: Pageable = PageRequest.of(pageNumber, pageSize)
        val transactions: Page<Transaction> = transactionService!!.findAllTransactions(pageable1)

        return ResponseEntity(transactions, HttpStatus.OK)
    }

    //return ResponseEntity(entities, HttpStatus.OK);
    //http://localhost:8080/get_by_account_name_owner/amazon.gift_brian
    @GetMapping(path = arrayOf("/get_by_account_name_owner/{accountNameOwner}"))
    fun findByAccountNameOwner(@PathVariable accountNameOwner: String): ResponseEntity<List<Transaction>> {
        return ResponseEntity.ok(StreamSupport
                .stream(transactionService!!.findByAccountNameOwnerIgnoreCaseOrderByTransactionDate(accountNameOwner).spliterator(), false)
                .collect(Collectors.toList()))
    }

    //TODO: ResponseEntity code fix
    //http://localhost:8080/select/340c315d-39ad-4a02-a294-84a74c1c7ddc
    @GetMapping(path = arrayOf("/select/{guid}"))
    fun findtTransaction(@PathVariable guid: String): Transaction {
        val transaction: Transaction
        LOGGER.info("getTransaction=" + guid)
        transaction = transactionService!!.findByGuid(guid)
        if (transaction.accountNameOwner != "" ) {
            return transaction
        } else {
            return Transaction()
        }
    }

    //http://localhost:8080/update/340c315d-39ad-4a02-a294-84a74c1c7ddc
    //TODO: ResponseEntity code fix
    @PutMapping(path = arrayOf("/update"), consumes = arrayOf("application/json"), produces = arrayOf("application/json"))
    fun updateTransaction(@RequestBody transaction: Transaction ) {
        //TODO: need to figure out how to perform this operation
        val transaction = transactionService!!.findByGuid("guid")
    }

    //http://localhost:8080/insert
    //TODO: ResponseEntity code fix
    @PostMapping(path = arrayOf("/insert"), consumes = arrayOf("application/json"), produces = arrayOf("application/json"))
    //@PostMapping("/insert")
    fun insertTransaction(@RequestBody transaction: Transaction) : String {
        val resultMessage = ResultMessage()
        var resultString: String = ""

        try {
            transactionService?.insertTransaction(transaction)
            resultMessage.message = "Successfully processed add message."
            resultMessage.resultCode = 0
            resultMessage.guid = transaction.guid!!
            resultMessage.setDate(ZonedDateTime.now())

            resultString = mapper.writeValueAsString(resultMessage)
            return resultString
        } catch (jpe: JsonProcessingException) {
            resultMessage.message = "Failure to processed add message: " + "Exception: " + jpe + " Exception message:" + jpe.message
            resultMessage.resultCode = 200
            resultString = mapper.writeValueAsString(resultMessage)
            return resultString
        } catch (e: Exception) {
            resultMessage.message = "Failure to processed add message: " + "Exception: " + e + " Exception message:" + e.message
            resultMessage.resultCode = 201
            resultString = mapper.writeValueAsString(resultMessage)
            return resultString
        }
    }

    //http://localhost:8080/delete/38739c5b-e2c6-41cc-82c2-d41f39a33f9a
    //@GetMapping(value = "/delete/{guid}")
    //TODO: ResponseEntity code fix
    @GetMapping(path = arrayOf("/delete/{guid}"))
    fun deleteTransaction(@PathVariable guid: String): String {
        val restResult = ResultMessage()
        var resultString : String = ""
        restResult.guid = guid

        LOGGER.info(guid)
        try {
            val transaction = transactionService!!.findByGuid(guid)
            LOGGER.info(transaction.description)
            LOGGER.info(transaction.accountNameOwner)
            LOGGER.info(transaction.guid)
            LOGGER.info("transactions.contains(transaction) is found.")

            transactionService!!.deleteByGuid(guid)
            restResult.message = "Successfully processed delete message."
            restResult.resultCode = 0
            restResult.setDate(ZonedDateTime.now())
            resultString = mapper.writeValueAsString(restResult)
            //return resultString
        } catch (ex: Exception) {
            LOGGER.info(ex.message)
            restResult.message = "Failure to processed delete message: " + "Exception: " + ex + " Exception message:" + ex.message
            restResult.resultCode = 100
            restResult.setDate(ZonedDateTime.now())
            resultString = mapper.writeValueAsString(restResult)
            //return resultString
        } finally {
            return resultString
        }
    }

    companion object {
        private val mapper = ObjectMapper()
    }
}
