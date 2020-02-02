package com.markd.applications.android.home.AdapterClasses;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.markd.applications.android.home.R;
import com.markd.applications.android.home.customer_menu_activities.ServiceHistoryActivity;
import com.markd.applications.android.home.data_objects.ContractorService;

import java.util.ArrayList;
import java.util.List;

public class ServiceHistoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EditHomeRecyclerAdapter";
    private ServiceHistoryActivity context;

    private final String PLUMBING_TITLE = "Plumbing Services";
    private final String HVAC_TITLE = "Hvac Services";
    private final String ELECTRICAL_TITLE = "Electrical Services";
    private final String PAINTING_TITLE = "Painting Services";
    private final String CONTRACTOR_TITLE = "Service History";

    private int PLUMBING_TITLE_INDEX = 0;
    private int HVAC_TITLE_INDEX;
    private int ELECTRICAL_TITLE_INDEX;
    private int PAINTING_TITLE_INDEX;
    private final int itemCount;

    private List<ContractorService> plumbingServices;
    private List<ContractorService> hvacServices;
    private List<ContractorService> electricalServices;
    private List<ContractorService> paintingServices;

    private boolean isContractor;
    private String contractorType;
    private List<ContractorService> contractorServices;


    public ServiceHistoryRecyclerViewAdapter(
            final ServiceHistoryActivity context,
            final String contractorType,
            @NonNull final List<ContractorService> services) {
        this.context = context;
        isContractor = true;
        this.contractorType = contractorType;
        if (services == null) {
            itemCount = 2;
            contractorServices = new ArrayList<>();
        } else {
            itemCount = 1 + services.size();
            contractorServices = services;
        }
        Log.d(TAG, "TOTAL ITEMS: " + itemCount);
    }

    public ServiceHistoryRecyclerViewAdapter(
            final ServiceHistoryActivity context,
            @NonNull final List<ContractorService> plumbingServices,
            @NonNull final List<ContractorService> hvacServices,
            @NonNull final List<ContractorService> electricalServices,
            @NonNull final List<ContractorService> paintingServices) {
        this.context = context;

        this.plumbingServices = plumbingServices;
        if (plumbingServices.size() == 0) {
            HVAC_TITLE_INDEX = 2 + PLUMBING_TITLE_INDEX;
        } else {
            HVAC_TITLE_INDEX = 1 + PLUMBING_TITLE_INDEX + plumbingServices.size();
        }

        this.hvacServices = hvacServices;
        if (hvacServices.size() == 0) {
            ELECTRICAL_TITLE_INDEX = 2 + HVAC_TITLE_INDEX;
        } else {
            ELECTRICAL_TITLE_INDEX = 1 + HVAC_TITLE_INDEX + hvacServices.size();
        }

        this.electricalServices = electricalServices;
        if (electricalServices.size() == 0) {
            PAINTING_TITLE_INDEX = 2 + ELECTRICAL_TITLE_INDEX;
        } else {
            PAINTING_TITLE_INDEX = 1 + ELECTRICAL_TITLE_INDEX + electricalServices.size();
        }

        this.paintingServices = paintingServices;
        if (paintingServices.size() == 0) {
            itemCount = 2 + PAINTING_TITLE_INDEX;
        } else {
            itemCount = 1 + PAINTING_TITLE_INDEX + paintingServices.size();
        }
        Log.d(TAG, "TOTAL ITEMS: " + itemCount);
    }
    @Override
    public int getItemCount() {
        return itemCount;
    }
    @Override
    public int getItemViewType(int position) {
        //Title Views
        if (position == PLUMBING_TITLE_INDEX ||
                position == HVAC_TITLE_INDEX ||
                position == ELECTRICAL_TITLE_INDEX ||
                position == PAINTING_TITLE_INDEX) {
            return R.layout.view_holder_title_button;
        }
        return R.layout.list_row_service;
    }
    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(viewType, parent, false);
        switch (viewType) {
            case R.layout.view_holder_title_button:
                return new TitleButtonViewHolder(view);
            case R.layout.list_row_service:
                return new ServiceHistoryViewHolder(view);
            default:
                throw new IllegalArgumentException(String.format("No view type for %d", viewType));
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (isContractor) {
            bindContractorData(viewHolder, position);
        } else {
            bindCustomerData(viewHolder, position);
        }

    }
    private void bindContractorData(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof TitleButtonViewHolder) {
            ((TitleButtonViewHolder) viewHolder).bindData(CONTRACTOR_TITLE, contractorType);
        } else if (viewHolder instanceof ServiceHistoryViewHolder) {
            if (contractorServices.size() == 0) {
                ((ServiceHistoryViewHolder) viewHolder).bindData();
            } else {
                int index = position - 1;
                ((ServiceHistoryViewHolder) viewHolder)
                        .bindData(contractorServices.get(index), contractorType, index);
            }
        } else {
            throw new IllegalArgumentException(String.format("No view holder for %d", position));
        }
    }
    private void bindCustomerData(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof TitleButtonViewHolder) {
            if (position == PLUMBING_TITLE_INDEX) {
                ((TitleButtonViewHolder) viewHolder).bindData(PLUMBING_TITLE, "plumber");
            } else if (position == HVAC_TITLE_INDEX) {
                ((TitleButtonViewHolder) viewHolder).bindData(HVAC_TITLE, "hvac");
            } else if (position == ELECTRICAL_TITLE_INDEX) {
                ((TitleButtonViewHolder) viewHolder).bindData(ELECTRICAL_TITLE, "electrician");
            } else if (position == PAINTING_TITLE_INDEX) {
                ((TitleButtonViewHolder) viewHolder).bindData(PAINTING_TITLE, "painter");
            }
        } else if (viewHolder instanceof ServiceHistoryViewHolder) {
            //PLUMBING SERVICE
            if(position < HVAC_TITLE_INDEX) {
                if (plumbingServices.size() == 0) {
                    ((ServiceHistoryViewHolder) viewHolder).bindData();
                } else {
                    int index = position - 1;
                    ((ServiceHistoryViewHolder) viewHolder)
                            .bindData(plumbingServices.get(index), "plumber", index);
                }
                return;
            }
            //HVAC SERVICE
            if (position > HVAC_TITLE_INDEX && position < ELECTRICAL_TITLE_INDEX) {
                if (hvacServices.size() == 0) {
                    ((ServiceHistoryViewHolder) viewHolder).bindData();
                } else {
                    // Position - 2 Title Rows - PLUMBING SERVICE Rows
                    int index = position - 2;
                    if (plumbingServices.size() == 0) {
                        index -= 1; //Minus one for the "No Services!" row
                    } else {
                        index -= plumbingServices.size();
                    }
                    ((ServiceHistoryViewHolder) viewHolder)
                            .bindData(hvacServices.get(index), "hvac", index);
                }
                return;
            }
            //ELECTRICAL SERVICE
            if (position > ELECTRICAL_TITLE_INDEX && position < PAINTING_TITLE_INDEX) {
                if (electricalServices.size() == 0) {
                    ((ServiceHistoryViewHolder) viewHolder).bindData();
                } else {
                    // Position - 3 Title Rows - PLUMBING SERVICE Rows - HVAC SERVICE Rows
                    int index = position - 3;
                    if (plumbingServices.size() == 0) {
                        index -= 1; //Minus one for the "No Services!" row
                    } else {
                        index -= plumbingServices.size();
                    }
                    if (hvacServices.size() == 0) {
                        index -= 1; //Minus one for the "No Services!" row
                    } else {
                        index -= hvacServices.size();
                    }
                    ((ServiceHistoryViewHolder) viewHolder)
                            .bindData(electricalServices.get(index), "electrician", index);
                }
                return;
            }
            //PAINTING SERVICE
            if (position > PAINTING_TITLE_INDEX) {
                if (paintingServices.size() == 0) {
                    ((ServiceHistoryViewHolder) viewHolder).bindData();
                } else {
                    // Position - 4 Title Rows - PLB Rows - HVAC Rows - ELEC Rows
                    int index = position - 4;
                    if (plumbingServices.size() == 0) {
                        index -= 1; //Minus one for the "No Services!" row
                    } else {
                        index -= plumbingServices.size();
                    }
                    if (hvacServices.size() == 0) {
                        index -= 1; //Minus one for the "No Services!" row
                    } else {
                        index -= hvacServices.size();
                    }
                    if (electricalServices.size() == 0) {
                        index -= 1; //Minus one for the "No Services!" row
                    } else {
                        index -= electricalServices.size();
                    }
                    ((ServiceHistoryViewHolder) viewHolder)
                            .bindData(paintingServices.get(index), "painter", index);
                }
            }
        } else {
            throw new IllegalArgumentException(String.format("No view holder for %d", position));
        }
    }

    class TitleButtonViewHolder extends RecyclerView.ViewHolder {
        private final ImageView button;
        private final TextView textView;

        TitleButtonViewHolder(final View v) {
            super(v);
            textView = v.findViewById(R.id.title);
            button = v.findViewById(R.id.button);
        }

        void bindData(final String title, final String serviceType) {
            textView.setText(title);
            button.setImageResource(R.drawable.add_button_round_black);
            button.setOnClickListener(v -> {
                context.addNewService(serviceType);
            });
        }
    }
    class ServiceHistoryViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView contractorTextView;
        private final TextView serviceDate;
        private final TextView serviceDescription;
        private ContractorService service;
        private String serviceType;
        private int index;

        ServiceHistoryViewHolder(final View v) {
            super(v);
            this.view = v;
            view.setClickable(true);
            view.setOnClickListener((view) -> {
                context.getServiceDetail(service, serviceType, index);
            });
            contractorTextView = v.findViewById(R.id.contractor_name);
            serviceDate = v.findViewById(R.id.service_date);
            serviceDescription = v.findViewById(R.id.service_description);
        }

        void bindData() {
            view.setClickable(false);
            contractorTextView.setText(R.string.no_services);
        }

        void bindData(@NonNull final ContractorService service,
                      final String serviceType,
                      final int index) {
            this.service = service;
            this.serviceType = serviceType;
            this.index = index;

            contractorTextView.setText(service.getContractor());
            serviceDate.setText(service.getDate());
            serviceDescription.setText(service.getComments());
        }
    }
}
