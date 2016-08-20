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

package pt.isel.s1516v.ps.apiaccess;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.TertuliaDetailsActivity;

public class TertuliasArrayAdapter extends RecyclerView.Adapter<TertuliasArrayAdapter.ViewHolder> {

    private Activity ctx;
    private TertuliaListItem[] tertulias;

    public TertuliasArrayAdapter(Activity ctx, TertuliaListItem[] tertulias) {
        this.ctx = ctx;
        this.tertulias = tertulias;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_tertulias_listview_row, parent, false);
        return new ViewHolder(itemView,
                R.id.ltlr_tertulia_name,
                R.id.ltlr_tertulia_subject,
                R.id.ltlr_next_event,
                R.id.ltlr_messages_count,
                R.id.ltlr_member_role,
                R.id.ltlr_event_label,
                R.id.ltlr_event_terminator,
                R.id.ltlr_message_label);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TertuliaListItem tertulia = tertulias[position];
        holder.tertuliaName.setText(tertulia.name);
        holder.tertuliaSubject.setText(tertulia.subject);
        String nextEventStr = "";
        if (tertulia.nextEventDate != null) {
            nextEventStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .format(tertulia.nextEventDate);
        }
        if (!TextUtils.isEmpty(tertulia.location.name)) {
            if (!TextUtils.isEmpty(nextEventStr))
                nextEventStr += " @ ";
            nextEventStr += tertulia.location.name;
        }
        if (!TextUtils.isEmpty(nextEventStr))
            holder.nextEvent.setText(nextEventStr);
        else {
            holder.eventLabel.setVisibility(View.GONE);
            holder.nextEvent.setVisibility(View.GONE);
            holder.eventSeparator.setVisibility(View.GONE);
        }
        if (tertulia.messagesCount > 0)
            holder.messagesCount.setText(String.valueOf(tertulia.messagesCount));
        else {
            holder.messageLabel.setVisibility(View.INVISIBLE);
            holder.messagesCount.setVisibility(View.INVISIBLE);
        }

        holder.memberRole.setText(tertulia.role_type);
        holder.links = tertulia.links;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAbort;
                ApiLink selectedLink = null;
                if (holder.links == null || holder.links.length == 0)
                    isAbort = true;
                else {
                    isAbort = true;
                    for (ApiLink link : holder.links)
                        if (link.rel.equals("self")) {
                            selectedLink = link;
                            isAbort = false;
                            break;
                        }
                }
                if (isAbort) {
                    Util.longSnack(view, R.string.activity_list_tertulias_toast_no_details);
                    return;
                }
                ApiLinks apiLinks = new ApiLinks(holder.links);
                Intent intent = new Intent(ctx, TertuliaDetailsActivity.class);
                intent.putExtra(TertuliaDetailsActivity.SELF_LINK, selectedLink);
                intent.putExtra(TertuliaDetailsActivity.INTENT_LINKS, apiLinks);
                ctx.startActivityForResult(intent, TertuliaDetailsActivity.ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tertulias.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tertuliaName,
                tertuliaSubject,
                nextEvent,
                messagesCount,
                memberRole,
                eventLabel,
                eventSeparator,
                messageLabel;
        private ApiLink[] links;

        public ViewHolder(View ctx, Integer... viewIds) {
            super(ctx);
            int i = 0;
            tertuliaName = (TextView) ctx.findViewById(viewIds[i++]);
            tertuliaSubject = (TextView) ctx.findViewById(viewIds[i++]);
            nextEvent = (TextView) ctx.findViewById(viewIds[i++]);
            messagesCount = (TextView) ctx.findViewById(viewIds[i++]);
            memberRole = (TextView) ctx.findViewById(viewIds[i++]);
            eventLabel = (TextView) ctx.findViewById(viewIds[i++]);
            eventSeparator = (TextView) ctx.findViewById(viewIds[i++]);
            messageLabel = (TextView) ctx.findViewById(viewIds[i]);
        }

        public ApiLink[] getLinks() {
            return links;
        }

    }
}
