/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package pt.isel.s1516v.ps.apiaccess.memberinvitation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.CircleTransform;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.TertuliaDetailsActivity;

public class ContactsArrayAdapter extends RecyclerView.Adapter<ContactsArrayAdapter.ViewHolder> {

    private Activity ctx;
    private ContactListItem[] contacts;
    private boolean[] selection;

    public ContactsArrayAdapter(Activity ctx, ContactListItem[] contacts) {
        this.ctx = ctx;
        this.contacts = contacts;
        selection = new boolean[contacts.length];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_search_contact_recycleview_row, parent, false);
        return new ViewHolder(itemView,
                R.id.scrr_photo,
                R.id.scrr_name,
                R.id.scrr_email,
                R.id.scrr_check);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ContactListItem contact = contacts[position];
        if (contact.photo != null)
            Picasso.with(ctx).load(contact.photo).transform(new CircleTransform()).into(holder.contactPicture);
        else
            Picasso.with(ctx).load(R.mipmap.tertulias).transform(new CircleTransform()).into(holder.contactPicture);
        holder.contactName.setText(contact.name);
        holder.contactEmail.setText(contact.email);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = !selection[position];
                selection[position] = isSelected;
                holder.isContactSelected.setChecked(isSelected);
                Util.logd(view.toString());
            }
        });
        holder.isContactSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selection[position] = view.isSelected();

                Util.logd(view.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView contactPicture;
        private final TextView contactName;
        private final TextView contactEmail;
        private final CheckBox isContactSelected;

        public ViewHolder(View ctx, Integer... viewIds) {
            super(ctx);
            int i = 0;
            contactPicture = (ImageView) ctx.findViewById(viewIds[i++]);
            contactName = (TextView) ctx.findViewById(viewIds[i++]);
            contactEmail = (TextView) ctx.findViewById(viewIds[i++]);
            isContactSelected = (CheckBox) ctx.findViewById(viewIds[i++]);
        }

    }

    public boolean[] getSelection() {
        return selection;
    }
}
