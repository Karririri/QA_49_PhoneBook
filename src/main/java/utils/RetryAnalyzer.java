package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    int retryCount = 0;

    private static  int maxTryValue = 3;

    public static void setMaxTryValue(int maxTryValue) {
        RetryAnalyzer.maxTryValue = maxTryValue;
    }

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (retryCount < maxTryValue){
            retryCount ++;
            return true;
        }
        return false;
    }
}
