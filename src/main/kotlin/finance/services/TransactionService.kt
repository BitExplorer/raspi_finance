package finance.services


import finance.models.Transaction
import finance.repositories.MongoTransactionRepository
import finance.repositories.TransactionRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.ArrayList
import java.util.function.Consumer

@Service
class TransactionService {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    internal var transactionRepository: TransactionRepository? = null

    @Autowired(required=false)
    internal var mongoTransactionRepository: MongoTransactionRepository? = null

    fun findAll(): List<Transaction> {
        val transactions = ArrayList<Transaction>()
        this.transactionRepository!!.findAll().forEach(Consumer<Transaction> { transactions.add(it) })
        return transactions
    }

    //fun fetchAccoutTotals(accountNameOwner: String): Double {
    //    return transactionRepository!!.fetchAccoutTotals(accountNameOwner)
    //}

    fun deleteByGuid(guid: String) {
        try {
            transactionRepository!!.deleteByGuid(guid)
        } catch (ex: Exception) {
            LOGGER.info(ex.message)
        }
    }

    //TODO: Debating on how to pass the updated fields to this method
    fun updateTransaction(guid: String, list: Map<String, String>) {
        val transaction: Transaction = findByGuid(guid);

        transaction.cleared = list["cleared"]!!.toInt()
        transactionRepository!!.saveAndFlush(transaction)
    }

    fun insertTransaction(transaction: Transaction) {
        val guid: String?  = transaction.guid;
        if (guid != null && guid.length > 0) {
            val transactionFound = transactionRepository?.findByGuid(guid)
            if( transactionFound == null ) {
                transactionRepository!!.saveAndFlush(transaction)
            } else {
                LOGGER.info("INFO: transactionRepository found duplicate.")
            }
        }
    }

    fun findByGuid(guid: String): Transaction {
        //TODO: what if a GUID is not found?
        return transactionRepository!!.findByGuid(guid)
    }

    fun findByAccountNameOwnerIgnoreCaseOrderByTransactionDate(accountNameOwner: String): List<Transaction> {
        return transactionRepository!!.findByAccountNameOwnerIgnoreCaseOrderByTransactionDate(accountNameOwner)
    }
}
