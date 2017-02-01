package com.drkhannah.concerts;

import com.drkhannah.concerts.data.TestConcertsContract;
import com.drkhannah.concerts.data.TestConcertsDbHelper;
import com.drkhannah.concerts.data.TestUriMatcher;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by dhannah on 1/24/17.
 */

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestConcertsDbHelper.class,
        TestUriMatcher.class,
        TestConcertsContract.class})

public class TestSuite {}

