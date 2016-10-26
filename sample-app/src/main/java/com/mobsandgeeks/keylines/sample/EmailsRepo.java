/*
 * Copyright (C) 2016 Ragunath Jawahar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobsandgeeks.keylines.sample;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ragunath Jawahar
 */
class EmailsRepo {

    /**
     * DO NOT DO THIS, EVER! IO ops on the main thread.
     */
    static List<Email> getEmails(Context context) {
        List<Email> emails = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("emails.txt")));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (TextUtils.isEmpty(line)) {
                    continue;
                }

                String[] parts = line.split("\\|" /* delimiter */);
                if (parts.length >= 3) {
                    emails.add(new Email(
                            parts[0].trim()   /* sender */,
                            parts[1].trim()   /* subject */,
                            parts[2].trim()   /* summary */,
                            parts.length == 4 /* read */)
                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { if (bufferedReader != null) bufferedReader.close(); }
            catch (IOException ignored) {}
        }
        return emails;
    }

    private EmailsRepo() {
        throw new AssertionError("No instances.");
    }

}
