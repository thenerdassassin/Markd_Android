package com.markd.applications.android.home.billing;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.markd.applications.android.home.R;

import java.util.Collections;
import java.util.List;

public class BillingClientHandler {
    private final String TAG = "BillingClientHandler";
    private final String contractorSubscriptionProductId;

    private Activity activity;
    private final BillingClient billingClient;
    private SkuDetails contractorSubscriptionSku;

    public BillingClientHandler(
            final Activity activity,
            final PurchasesUpdatedListener purchasesUpdatedListener) {
        this.activity = activity;
        contractorSubscriptionProductId = activity
                .getString(R.string.annual_contractor_subscription);

        // https://developer.android.com/google/play/billing/billing_library_overview#Connect
        billingClient = BillingClient
                .newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(billingClientStateListener);
    }

    public void endConnection()  {
        billingClient.endConnection();
    }

    // https://developer.android.com/google/play/billing/billing_library_overview#Query
    private void queryForProductDetails() {
        billingClient.querySkuDetailsAsync(
                getSkuDetailsParams(getSkuList(), BillingClient.SkuType.SUBS),
                skuDetailsListener);
    }

    private SkuDetailsParams getSkuDetailsParams(final List<String> skuList, final String skuType) {
        return SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(skuType).build();
    }

    private List<String> getSkuList() {
        return Collections.singletonList(contractorSubscriptionProductId);
    }

    private BillingClientStateListener billingClientStateListener = new BillingClientStateListener() {
        @Override
        public void onBillingSetupFinished(BillingResult billingResult) {
            Log.d(TAG, "BillingSetupFinished: " + billingResult.getResponseCode());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "BillingClient.BillingResponseCode.OK");
                queryForProductDetails();
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR) {
                Log.e(TAG, billingResult.getDebugMessage());
            }
        }

        @Override
        public void onBillingServiceDisconnected() {
            // TODO: billingServiceDisconnected
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
        }
    };

    private SkuDetailsResponseListener skuDetailsListener = new SkuDetailsResponseListener() {
        @Override
        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skus) {
            Log.d(TAG, "Got skus");
            if (billingResult == null) {
                Log.e(TAG, "Billing result is null");
                return;
            }
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Not ok: " + billingResult.getDebugMessage());
                return;
            }
            if(skus == null) {
                Log.e(TAG, "Null skus :(");
                return;
            }

            Log.d(TAG, "SkuDetails: " + skus.toString());
            // Process the result.
            for(SkuDetails skuDetails : skus) {
                Log.d(TAG, skuDetails.getDescription());
                Log.d(TAG, skuDetails.getPrice());
                Log.d(TAG, skuDetails.getTitle());
                if (skuDetails.getSku().equals(contractorSubscriptionProductId)) {
                    contractorSubscriptionSku = skuDetails;
                }
            }
        }
    };

    public void launchBillingFlow() {
        final BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(contractorSubscriptionSku)
                .build();
        // Save purchase token?
        billingClient.launchBillingFlow(activity, flowParams);
    }

    public void handlePurchase(final Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(
                        acknowledgePurchaseParams, (BillingResult billingResult) -> {
                            Log.d(TAG, "Acknowledged purchase: " + billingResult.getResponseCode());
                        });
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            // Here you can confirm to the user that they've started the pending
            // purchase, and to complete it, they should follow instructions that
            // are given to them. You can also choose to remind the user in the
            // future to complete the purchase if you detect that it is still
            // pending.
            Toast.makeText(activity,
                    "Transaction is pending. Make sure to complete purchase.",
                    Toast.LENGTH_LONG).show();
        }
    }
    // TODO: Verify Purchase -
    //      https://developer.android.com/google/play/billing/billing_library_overview#Verify
    // TODO: Billing Subscription Specific -
    //      https://developer.android.com/google/play/billing/billing_subscriptions
}
