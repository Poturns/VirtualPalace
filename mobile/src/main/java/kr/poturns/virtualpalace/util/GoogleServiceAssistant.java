package kr.poturns.virtualpalace.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import kr.poturns.virtualpalace.data.LocalArchive;

/**
 * Created by YeonhoKim on 2015-07-20.
 */
public class GoogleServiceAssistant {

    private final Context mContextF;

    private Account mGoogleAccount;

    public GoogleServiceAssistant(Context context) {
        this(context, (Account) null);
    }

    public GoogleServiceAssistant(Context context, String accountStr) {
        mContextF = context;

        if (accountStr != null) {
            String[] splits = accountStr.split("@");

            if ( splits.length > 1) {
                String type = new StringBuffer(splits[1]).reverse().toString();

                if ("com.google".equals(type)) {
                    setGoogleAccount(new Account(splits[0], type));
                    return;
                }
            }
        }

        requestGoogleAccountSelection();
    }

    public GoogleServiceAssistant(Context context, Account account) {
        mContextF = context;

        if (account == null || !"com.google".equals(account.type))
            requestGoogleAccountSelection();
        else
            setGoogleAccount(account);
    }

    public void requestGoogleAccountSelection() {
        // Google Account 접근 및 설정
        AccountManager accountManager = (AccountManager) mContextF.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] googleAccounts = accountManager.getAccountsByType("com.google");


    }

    void setGoogleAccount(Account account) {
        if (account != null && "com.google".equals(account.type)) {
            mGoogleAccount = account;

            LocalArchive.getInstance(mContextF)
                        .putSystemStringValue(LocalArchive.ISystem.ACCOUNT, account.name);
        }
    }

    Account getGoogleAccount() {
        return mGoogleAccount;
    }

}
