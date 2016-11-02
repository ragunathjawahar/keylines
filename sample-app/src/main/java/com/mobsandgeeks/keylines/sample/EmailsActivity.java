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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mobsandgeeks.keylines.sdk.Design;

import static com.mobsandgeeks.keylines.sample.EmailsRepo.getEmails;

/**
 * @author Ragunath Jawahar
 */
@Design(R.raw.activity_emails)
public class EmailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emails);
        setTitle(R.string.inbox);

        setupEmailsRecyclerView();
        setupFab();
    }

    private void setupEmailsRecyclerView() {
        RecyclerView emailsRecyclerView = (RecyclerView) findViewById(R.id.emailsRecyclerView);
        emailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emailsRecyclerView.setHasFixedSize(true);
        emailsRecyclerView.setAdapter(new EmailsAdapter(getEmails(this)));
    }

    private void setupFab() {
        Drawable composeDrawable = AppCompatDrawableManager.get()
                .getDrawable(this, R.drawable.ic_compose);
        ((FloatingActionButton) findViewById(R.id.composeFab)).setImageDrawable(composeDrawable);
    }

}
