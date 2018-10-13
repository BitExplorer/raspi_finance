package finance.processors

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import finance.models.Transaction
import org.apache.camel.Exchange
import org.apache.camel.Processor
//import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException

@Component
open class JsonTransactionProcessor : Processor {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    @Throws(Exception::class)
    override fun process(exchange: Exchange) {
        val transactions: Array<Transaction>

        try {
            val payload = exchange.`in`.getBody(String::class.java)
            transactions = mapper.readValue(payload, Array<Transaction>::class.java)
            exchange.`in`.body = transactions
        } catch (upe: UnrecognizedPropertyException) {
            LOGGER.info("UnrecognizedPropertyException")
            upe.printStackTrace()
            throw upe
        } catch (ioe: IOException) {
            LOGGER.info("IOException")
            ioe.printStackTrace()
        } catch (e: Exception) {
            LOGGER.info("UnrecognizedPropertyException")
            e.printStackTrace()
        }

    }

    companion object {
        val mapper = ObjectMapper()
    }
}
