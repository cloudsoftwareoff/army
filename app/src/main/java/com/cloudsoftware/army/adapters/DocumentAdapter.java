package com.cloudsoftware.army.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudsoftware.army.R;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {

    private List<String> documentUrls;
    private Context context;

    public DocumentAdapter(Context context, List<String> documentUrls) {
        this.context = context;
        this.documentUrls = documentUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String documentUrl = documentUrls.get(position);
        holder.documentUrlTextView.setText(documentUrl);



        holder.downloadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(documentUrl));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return documentUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView documentUrlTextView;

        ImageView downloadButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            documentUrlTextView = itemView.findViewById(R.id.document_url_text_view);

            downloadButton = itemView.findViewById(R.id.download_button);
        }
    }
}
