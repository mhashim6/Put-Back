package mhashim6.android.putback

import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType.INAPP

object DonationsRepository {

    var billingReady = false
    var productsReady = false

    private lateinit var billingClient: BillingClient

    private val productsIds = listOf("android.test.purchased")
    val products = mutableListOf<SkuDetails>()

    fun init(context: Context) {
        billingClient = BillingClient.newBuilder(context).setListener(purchasesUpdatedListener).build()
        billingClient.startConnection(clientStateListener)
    }

    private val clientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(responseCode: Int) {
            debug("billing response:  $responseCode")
            billingReady = (responseCode == BillingClient.BillingResponse.OK)

            if (billingReady) {
                val params = SkuDetailsParams.newBuilder()
                params.setSkusList(productsIds).setType(INAPP)
                billingClient.querySkuDetailsAsync(params.build()) { response, skuDetailsList ->

                    productsReady = (response == BillingClient.BillingResponse.OK)
                    debug("sku query response:  $response")
                    if (productsReady)
                        products.addAll(skuDetailsList)
                }
            }
        }

        override fun onBillingServiceDisconnected() {
            debug("called")
            billingClient.startConnection(this)
        }
    }


    private val purchasesUpdatedListener = PurchasesUpdatedListener { responseCode, purchases ->
        //TODO
    }


}