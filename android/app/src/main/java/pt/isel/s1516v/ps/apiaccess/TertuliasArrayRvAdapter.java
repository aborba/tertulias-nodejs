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
import pt.isel.s1516v.ps.apiaccess.support.domain.ReadTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.TertuliaDetailsActivity;

public class TertuliasArrayRvAdapter extends RecyclerView.Adapter<TertuliasArrayRvAdapter.ViewHolder> {

    private Activity ctx;
    private ReadTertulia[] tertulias;

    public TertuliasArrayRvAdapter(Activity ctx, ReadTertulia[] tertulias) {
        this.ctx = ctx;
        this.tertulias = tertulias;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tertulias_listview_row, parent, false);
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
        ReadTertulia tertulia = tertulias[position];
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
                Intent intent = new Intent(ctx, TertuliaDetailsActivity.class);
                intent.putExtra(TertuliaDetailsActivity.SELF_LINK, selectedLink);
                intent.putExtra(TertuliaDetailsActivity.LINKS_LABEL, holder.links);
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
