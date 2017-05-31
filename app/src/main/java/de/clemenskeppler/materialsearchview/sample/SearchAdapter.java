package de.clemenskeppler.materialsearchview.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Clemens Keppler on 31.05.2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {
  private final LayoutInflater inflater;
  private ArrayList<String> results;

  public SearchAdapter(@NonNull Context context, @NonNull ArrayList<String> previousResults) {
    inflater = LayoutInflater.from(context);
    results = previousResults;
  }

  @Override public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = inflater.inflate(R.layout.search_result_item, parent, false);
    return new SearchHolder(itemView);
  }

  @Override public void onBindViewHolder(SearchHolder holder, int position) {
    final String result = results.get(position);
    holder.result.setText(result);
  }

  @Override public int getItemCount() {
    return results.size();
  }

  public void add(String result) {
    results.add(result);
    notifyItemInserted(results.size() - 1);
  }

  class SearchHolder extends RecyclerView.ViewHolder {
    TextView result;

    SearchHolder(View itemView) {
      super(itemView);
      result = (TextView) itemView.findViewById(R.id.result);
    }
  }
}
