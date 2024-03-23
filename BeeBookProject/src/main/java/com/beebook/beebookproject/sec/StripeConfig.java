package com.beebook.beebookproject.sec;



public class StripeConfig {

    private static String stripeApiKey = "sk_test_51OpWA3C4v7bv28irXzGkoLSZouDkAwGOTOHcCmPKMaUIaiqX2VKvzc1ifzbUAJRH7QPhFDCg63cPmpUs7PgRzuCp00H1btpALk";
    private static String stripePublishableKey = "pk_test_51OpWA3C4v7bv28irGba1ePhggthlIYDhwosRaKGTVTNHRXie4215KQQUEiJePbVgeTuioz4xi8puDS3sqLRO5WaM00FNcONTDm";

    public static String getStripeApiKey() {
        return stripeApiKey;
    }

    public static String getStripePublishableKey() {
        return stripePublishableKey;
    }
}
