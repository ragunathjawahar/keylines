package com.mobsandgeeks.keylines;

/**
 * @author Ragunath Jawahar
 */
interface Shared {

    String PACKAGE_NAME = KeylinesApplication.class.getPackage().getName();
    String NAMESPACE_ACTION = PACKAGE_NAME + ".intent.action";
    String NAMESPACE_EXTRA = PACKAGE_NAME + ".intent.extra";

}