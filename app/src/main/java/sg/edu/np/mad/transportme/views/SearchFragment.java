package sg.edu.np.mad.transportme.views;

import static android.icu.text.RelativeDateTimeFormatter.Direction.THIS;
import static sg.edu.np.mad.transportme.views.LoadingScreen.globalBusStops;

import android.app.ProgressDialog;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import sg.edu.np.mad.transportme.BusStop;
import sg.edu.np.mad.transportme.BusStopAdapter;
import sg.edu.np.mad.transportme.R;
import sg.edu.np.mad.transportme.api.ApiBusStopService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Geocoder geocoder2;
    public SearchFragment() {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        /**/

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String busNumbers[] = new String[] {"2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","69","70","71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86","87","88","89","90","91","92","93","94","95","96","97","98","99","100","101","102","103","105","106","107","109","110","111","112","113","114","115","116","117","118","119","120","121","122","123","124","125","127","129","130","131","132","133","134","135","136","137","138","139","140","141","142","143","145","147","150","151","153","154","155","156","157","158","159","160","161","162","163","165","166","167","168","169","170","171","172","173",
                "174","175","176","177","178","179","180","181","182","183","184","185","186","187","188","189","190","191","192","193","194","195","196","197","198","199","200","201","222","228","229","231","232","235","238","240","241","242","246","247","248","249","251","252","253","254","255","257","258","261","262","265","268","269","272","273","282","284","285","291","292","293","298","300","301","302","307","315","317","324","325","329","333","334","335","354","358","359","371","372","374","381","382","384","386","400","401","403","405","410","502","506","513","518","651","652","653","654","655","656","657","660","661","663","665","666","667","668","670","671","672","800","801","803","804","805","806","807","811","812","825","851","852","853","854","855","856","857","858","859","860","882","883","900","901","902","903","904","911","912","913","920","922","925","926","927","941","944","945",
                "947","950","960","961","962","963","964","965","966","969","970","972","973","974","975","976","979","980","981","983","985","990","991","154A","154B","142A","143M","145A","125A","127A","12e","130A","131A","131M","10e","112A","113A","114A","116A","117A","117M","147A","14A","14e","137A","138A","138B","139M","13A","118A","118B","123M","158A","159A","159B","15A","160A","160M","105B","106A","107M","109A","100A","169A","169B","16M","196A","196e","198A","1N","240A","240M","241A","243G","243W","170X","173A","162M","163A","178A","179A","17A","180A","181M","182M","183B","174e","200A","21A","222A","222B","225G","225W","190A","195A","188e","188R","189A","33A","33B","34A","34B","35M","36A","518A","51A","53A","53M","36B","382A","382G","382W","386A","3A","3N","410G","410W","43e","307A","307T","30e","31A","268A","268B","268C","269A","27A","43M","45A","4N","29A","2A","2N","302A","502A","248M","73T",
                "62A","63A","63M","850E","851e","853M","55B","58A","58B","70A","70B","70M","72A","72B","68A","68B","6N","5N","60A","60T","78A","79A","7A","7B","807A","807B","80A","811A","811T","812T","83T","858A","858B","859A","859B","859T","85A","860T","868E","901M","903M","90A","911T","912A","912B","912M","913M","913T","882A","883B","883M","88A","88B","89A","89e","900A","982E","983A","98A","98B","98M","969A","96A","96B","972A","972M","973A","974A","975A","975B","975C","97e","95B","960e","961M","854e","857A","857B","962B","962C","963e","963R","965A","965T","966A","925A","925M","92A","92B","92M","94A","951E","NR8","991A","991B","991C","9A","CT18","CT8","NR1","NR2","NR3","NR5","NR6"};
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText searchBar = (EditText) view.findViewById(R.id.editTextSearch);
        /*Button GoToMap = view.findViewById(R.id.buttonFind);*/
       /* Double latitude;
        Double longitude;
        GoogleMap map = ((SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMapAsync();*/
        // On touch listener for icon at the side of the textview
        ArrayList<String> searchStrings = new ArrayList<String>();
        for (String busNo : busNumbers){
            searchStrings.add(busNo);
        }
        for (int i =0 ; i<globalBusStops.size();i++){
            searchStrings.add(globalBusStops.get(i).getDescription());
            searchStrings.add(globalBusStops.get(i).getBusStopCode());

        }

        AutoCompleteTextView searchAutoComplete = view.findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, searchStrings);
        searchAutoComplete.setAdapter(adapter);

        // On click listner for keyboard search button
        searchAutoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = String.valueOf(searchAutoComplete.getText());
                    if (searchText.equals("")){
                        Toast.makeText(getContext(),"No Bus Stops",Toast.LENGTH_LONG).show();
                        return true;
                    }
                    searchText = searchText.substring(0, 1).toUpperCase() + searchText.substring(1);
                    ProgressDialog progressDialog = new ProgressDialog(getContext(),R.style.MyAlertDialogStyle);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(
                            android.R.color.transparent
                    );
                    ArrayList<BusStop> searchBusStops = new ArrayList<>();
                    for (int i = 0; i<globalBusStops.size();i++) {  //Compare search term with all description and Roadname in globalBusStop
                        if (globalBusStops.get(i).getDescription().toLowerCase().contains(searchText.toLowerCase()) || globalBusStops.get(i).getRoadName().equalsIgnoreCase(searchText)){
                            searchBusStops.add(globalBusStops.get(i));
                        }
                    }


                    for (int i = 0; i<globalBusStops.size();i++){ // Find all bus stop with Similar bus stop code as search term
                        if (globalBusStops.get(i).getBusStopCode().equals(searchText)) {
                            searchBusStops.add(globalBusStops.get(i));
                        }
                    }
                    ApiBusStopService apiBusStopService = new ApiBusStopService(getActivity());
                    for (String busNo : busNumbers){
                        if (searchText.equals(busNo)){
                            apiBusStopService.getBusRoute(searchText,new ApiBusStopService.VolleyResponseListener3() { //Call API for nearby bus stops
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(getActivity(),"Cannot Get Bus Route, Check Location and Connection",Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onResponse(ArrayList<BusStop> busStopRouteLoaded) {
                                    apiBusStopService.getBusService(busStopRouteLoaded, new ApiBusStopService.VolleyResponseListener2() {
                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(getActivity(), "Cannot Get Bus Stops, Check Location and Connection", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onResponse(ArrayList<BusStop> busStopsLoaded) {
                                            RecyclerView rv = view.findViewById(R.id.searchrecyclerView);
                                            BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,getActivity());
                                            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
                                            rv.setAdapter(adapter);
                                            rv.setLayoutManager(layout);
                                            progressDialog.dismiss();
                                        }
                                    });


                                }
                            });
                            searchText ="Bus " + searchText ;
                        }
                    }
                    if (searchText.contains("Bus")){
                        Toast.makeText(getContext(), "Showing Bus Stops for " + searchText + " Might take a while", Toast.LENGTH_LONG).show();
                    }
                    /*MainActivity main = (MainActivity) getActivity();*/
                    else if(searchBusStops.size() > 90){ // Check if search term is too General like (Punggol), API call can only handle about 90 bus stops at a time
                        Toast.makeText(getContext(), "Please be more specific", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    else if (searchBusStops.size()>0){
                        if (searchBusStops.size() > 20){
                            Toast.makeText(getContext(),"More than 20 bus stops found, Might take a while",Toast.LENGTH_LONG).show();
                        }

                        apiBusStopService.getBusService(searchBusStops,new ApiBusStopService.VolleyResponseListener2() {
                            @Override
                            public void onError(String message) {
                                Log.d("Yes","api fail");
                            }
                            @Override
                            public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                RecyclerView rv = view.findViewById(R.id.searchrecyclerView); //Load searched bus stops into recycler view
                                BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,getActivity());
                                LinearLayoutManager layout = new LinearLayoutManager(getActivity());
                                rv.setAdapter(adapter);
                                rv.setLayoutManager(layout);
                                progressDialog.dismiss();
                            }
                        });
                    }



                    else{
                        Toast.makeText(getContext(), "No Matching Bus Stops or Service", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    return true;
                }
                return false;
            }
        });


        return view;
    }
}