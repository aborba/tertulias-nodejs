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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.CircleTransform;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class MembersArrayAdapter extends RecyclerView.Adapter<MembersArrayAdapter.ViewHolder> {

    private Activity ctx;
    private ApiMember[] members;
    private boolean[] selection;

    public MembersArrayAdapter(Activity ctx, ApiMember[] members) {
        this.ctx = ctx;
        this.members = members;
        selection = new boolean[members.length];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_member_recycleview_row, parent, false);
        return new ViewHolder(itemView,
                R.id.mrr_photo,
                R.id.mrr_name,
                R.id.mrr_role,
                R.id.mrr_email,
                R.id.mrr_check);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ApiMember member = members[position];
        if (member.photo != null)
            Picasso.with(ctx).load(member.photo).transform(new CircleTransform()).into(holder.memberPicture);
        else
            Picasso.with(ctx).load(R.mipmap.tertulias).transform(new CircleTransform()).into(holder.memberPicture);
        holder.memberName.setText(member.getName());
        holder.memberRole.setText(String.format("[ %-8s ]", member.role));
        holder.memberEmail.setText(member.email);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = !selection[position];
                selection[position] = isSelected;
                holder.isMemberSelected.setChecked(isSelected);
                Util.logd(view.toString());
            }
        });
        holder.isMemberSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selection[position] = view.isSelected();

                Util.logd(view.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView memberPicture;
        private final TextView memberName;
        private final TextView memberRole;
        private final TextView memberEmail;
        private final CheckBox isMemberSelected;

        public ViewHolder(View ctx, Integer... viewIds) {
            super(ctx);
            int i = 0;
            memberPicture = (ImageView) ctx.findViewById(viewIds[i++]);
            memberName = (TextView) ctx.findViewById(viewIds[i++]);
            memberRole = (TextView) ctx.findViewById(viewIds[i++]);
            memberEmail = (TextView) ctx.findViewById(viewIds[i++]);
            isMemberSelected = (CheckBox) ctx.findViewById(viewIds[i++]);
        }

    }

    public boolean[] getSelection() {
        return selection;
    }
}
