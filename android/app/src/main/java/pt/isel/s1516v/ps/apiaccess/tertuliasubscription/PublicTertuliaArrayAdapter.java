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

package pt.isel.s1516v.ps.apiaccess.tertuliasubscription;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public class PublicTertuliaArrayAdapter extends RecyclerView.Adapter<PublicTertuliaArrayAdapter.ViewHolder> {

    private Activity ctx;
    private PublicTertulia[] tertulias;

    public PublicTertuliaArrayAdapter(Activity ctx, PublicTertulia[] tertulias) {
        this.ctx = ctx;
        this.tertulias = tertulias;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_search_public_tertulia_recycleview_row, parent, false);
        return new ViewHolder(itemView,
                R.id.sprr_tertulia_name,
                R.id.sprr_tertulia_subject,
                R.id.sprr_location_name);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PublicTertulia publicTertulia = tertulias[position];
        holder.tertuliaName.setText(publicTertulia.name);
        holder.tertuliaSubject.setText(publicTertulia.subject);
        holder.locationName.setText(publicTertulia.location);
        holder.links = publicTertulia.links;
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
                Intent intent = new Intent(ctx, PublicTertuliaDetailsActivity.class);
                intent.putExtra(PublicTertuliaDetailsActivity.SELF_LINK, selectedLink);
                intent.putExtra(PublicTertuliaDetailsActivity.LINKS, publicTertulia.links);
                ctx.startActivityForResult(intent, PublicTertuliaDetailsActivity.ACTIVITY_REQUEST_CODE);
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
                locationName;
        private ApiLink[] links;

        public ViewHolder(View ctx, Integer... viewIds) {
            super(ctx);
            int i = 0;
            tertuliaName = (TextView) ctx.findViewById(viewIds[i++]);
            tertuliaSubject = (TextView) ctx.findViewById(viewIds[i++]);
            locationName = (TextView) ctx.findViewById(viewIds[i++]);
        }

        public ApiLink[] getLinks() {
            return links;
        }

    }
}
