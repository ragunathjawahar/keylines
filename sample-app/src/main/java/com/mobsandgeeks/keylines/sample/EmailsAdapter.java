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
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author Ragunath Jawahar
 */
class EmailsAdapter extends RecyclerView.Adapter<EmailsAdapter.EmailViewHolder> {

    private List<Email> emails;
    private LayoutInflater layoutInflater;

    EmailsAdapter(List<Email> emails) {
        this.emails = emails;
    }

    @Override
    public EmailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context);
        }

        View itemView = layoutInflater.inflate(R.layout.list_item_email, parent, false);
        return new EmailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EmailViewHolder holder, int position) {
        holder.bind(emails.get(position));
    }

    @Override
    public int getItemCount() {
        return emails != null ? emails.size() : 0;
    }

    static class EmailViewHolder extends RecyclerView.ViewHolder {

        private final TextView senderTextView;
        private final TextView summaryTextView;
        private final TextView subjectTextView;

        private final AvatarDrawable avatarDrawable;

        EmailViewHolder(View itemView) {
            super(itemView);
            ImageView avatarImageView = (ImageView) itemView.findViewById(R.id.avatarImageView);
            senderTextView = (TextView) itemView.findViewById(R.id.senderTextView);
            summaryTextView = (TextView) itemView.findViewById(R.id.summaryTextView);
            subjectTextView = (TextView) itemView.findViewById(R.id.subjectTextView);

            // Properties
            avatarDrawable = new AvatarDrawable();
            avatarImageView.setImageDrawable(avatarDrawable);
        }

        void bind(Email email) {
            // Image
            avatarDrawable.setAvatarText(email.sender);

            // Text
            senderTextView.setText(email.sender);
            subjectTextView.setText(email.subject);
            summaryTextView.setText(email.summary);

            // Text styling
            int style = !email.read ? Typeface.BOLD : Typeface.NORMAL;
            senderTextView.setTypeface(null, style);
            subjectTextView.setTypeface(null, style);
        }

    }

}
