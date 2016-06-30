package pt.isel.s1516v.ps.apiaccess;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public class TertuliasArrayAdapter extends ArrayAdapter<Tertulia> {

    private Activity ctx;
    private Tertulia[] tertulias;

    public TertuliasArrayAdapter(Activity ctx, Tertulia[] tertulias) {
        super(ctx, -1, tertulias);
        this.ctx = ctx;
        this.tertulias = tertulias;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = ctx.getLayoutInflater();
            rowView = inflater.inflate(R.layout.tertulias_listview_row, null);
            viewHolder = new ViewHolder(rowView,
                    R.id.ltlr_tertulia_name,
                    R.id.ltlr_tertulia_subject,
                    R.id.ltlr_next_event,
                    R.id.ltlr_messages_count,
                    R.id.ltlr_member_role,
                    R.id.ltlr_event_label,
                    R.id.ltlr_event_terminator,
                    R.id.ltlr_message_label);
            rowView.setTag(viewHolder);
        }

        if (viewHolder == null) viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.updateViews(tertulias[position]);
        return rowView;
    }

    public class ViewHolder {
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

        public void updateViews(Tertulia tertulia) {
            tertuliaName.setText(tertulia.name);

            if (TextUtils.isEmpty(tertulia.subject)) tertuliaSubject.setVisibility(View.INVISIBLE);
            else tertuliaSubject.setText("\"" + tertulia.subject + "\"");

            String nextEventStr = "";

            if (tertulia.nextEventDate != null)
                nextEventStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(tertulia.nextEventDate);
            if (!TextUtils.isEmpty(tertulia.location.name)) {
                if (!TextUtils.isEmpty(nextEventStr))
                    nextEventStr += " @ ";
                nextEventStr += tertulia.location.name;
            }
            if (!TextUtils.isEmpty(nextEventStr))
                nextEvent.setText(nextEventStr);
            else {
                eventLabel.setVisibility(View.GONE);
                nextEvent.setVisibility(View.GONE);
                eventSeparator.setVisibility(View.GONE);
            }

            if (tertulia.messagesCount > 0)
                messagesCount.setText(String.valueOf(tertulia.messagesCount));
            else {
                messageLabel.setVisibility(View.INVISIBLE);
                messagesCount.setVisibility(View.INVISIBLE);
            }
            memberRole.setText(tertulia.role_type);
            links = tertulia.links;
        }

        public ApiLink[] getLinks() {
            return links;
        }
    }
}
