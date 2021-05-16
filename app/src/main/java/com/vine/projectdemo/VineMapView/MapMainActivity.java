package com.vine.projectdemo.VineMapView;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewDebug;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qozix.tileview.TileView;
import com.qozix.tileview.hotspots.HotSpot;
import com.qozix.tileview.markers.MarkerLayout;
import com.vine.projectdemo.HomeFragment;
import com.vine.projectdemo.MainActivity;
import com.vine.projectdemo.R;
import com.vine.projectdemo.VineJsonParsing.JSONMainActivity;
import com.vine.projectdemo.VinePHPMySQL.PHPMainActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.lang.*;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.res.AssetManager;

import static com.vine.projectdemo.R.id.imageView;

/**
 * 含室內地圖
 */
public class MapMainActivity extends AppCompatActivity implements LocationListener {

    Timer timer = new Timer(true);

    int Doflag = 0, Plusflag = 0; //  跨區旗標
    int StartPointMin = 0, EndPointMin = 0;  // 最近短距離的 起點 終點
    int ListStFlag = 0;
    private String StartString;
    private String EndString;
    String[] AfterSplitStartString;
    String[] AfterSplitEndString;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Dijkstra ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//    private static final int INF = Integer.MAX_VALUE;   // 最大值
//    int[] mVexs = new int[125];// 純標記
//    int mMatrix[][]=new int[mVexs.length][mVexs.length];
//    int vs ;//起始點
//    int[] prev = new int[mVexs.length];
//    int[] dist = new int[mVexs.length];
//    int[] parent = new int[mVexs.length];
    //END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Dijkstra ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Dijkstra 未定大小~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static final int INF = Integer.MAX_VALUE;   // 最大值
    int[] mVexs;// 純標記

    int mMatrix[][];
    int vs;//起始點
    int[] prev;
    int[] dist;
    int[] parent;
    //END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Dijkstra 未定大小~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //private int[][] Loaddata; //

//    private double[][] datalist= {
//
//            {120.785537610585,24.5444640572263},{120.786122268108,24.5446147592479},{120.787774561107,24.5443779417854},{120.787977920245,24.5445609370974},{120.788143149545,24.5445717015275},
//            {120.788270249006,24.5443994706456},{120.786999254392,24.5442918263444},{120.788143149545,24.5442380041938},{120.787990630191,24.5442164753336},{120.788867616475,24.5442810619143},
//            {120.788829486636,24.5441518887529},{120.790176740928,24.5433768497844},{120.790329260281,24.5434844940856},{120.790405519958,24.5433876142145},{120.790265710551,24.5433014987736},
//            {120.79076139845,24.542698690687},{120.79142231565,24.5418913584282},{120.791473155434,24.5420851181703},{120.791396895757,24.5418052429872},{120.791473155434,24.5419236517185},
//            {120.791854453819,24.5408579731369},{120.792006973172,24.5402982227708},{120.792223042257,24.5393940106409},{120.791066437158,24.5385651495219},{120.790494489581,24.5389957267266},
//            {120.789998801682,24.5396092992433},{120.789490403836,24.5404273959322},{120.78987170222,24.5406426845345},{120.790265710551,24.5408902664273},{120.790303840389,24.5411163194597},
//            {120.790443649797,24.5411809060404},{120.789986091736,24.5414930745138},{120.790125901143,24.5415791899548},{120.789630213243,24.541966709439},{120.789757312705,24.5420635893101},
//            {120.790621589043,24.5425587530955},{120.788155859491,24.5438074269891},{120.788308378845,24.5438504847096},{120.787977920245,24.5437320759783},{120.787558492022,24.5435060229458},
//            {120.785715549831,24.543979657871},{120.787685591484,24.5426341041063},{120.788524447929,24.5416545409656},{120.788702387175,24.5414392523632},{120.789363304374,24.5405565690936},
//            {120.789757312705,24.540771857696},{120.789134525344,24.5416653053957},{120.788931166206,24.541869829568},{120.788549867821,24.5424403443642},{120.788804066744,24.5427309839774},
//            {120.788613417552,24.5430216235905},{120.788295668898,24.5433768497844},{120.788232119168,24.5451852740442},{120.788702387175,24.5460248995933},{120.788854906529,24.5459710774428},
//            {120.790888497912,24.5457665532705},{120.790888497912,24.5451206874635},{120.790774108396,24.5451529807538},{120.791307926134,24.545077629743},{120.790583459204,24.5439366001505},
//            {120.79076139845,24.5439150712903},{120.791041017265,24.5437428404084},{120.791180826673,24.5433983786447},{120.791231666458,24.5431077390315},{120.791295216188,24.5427525128376},
//            {120.791625674788,24.5422142913317},{120.791663804626,24.5419021228583},{120.79165109468,24.5431400323218},{120.791600254896,24.5434522007952},{120.791600254896,24.5440011867312},
//            {120.791701934465,24.5442057109035},{120.791536705165,24.5443994706456},{120.786617956007,24.5442057109035},{120.787355132884,24.5431400323218},{120.789007425882,24.5433768497844},
//            {120.789274334751,24.5431400323218},{120.789947961897,24.5404919825129},{120.790138611089,24.5406319201044},{120.790227580712,24.5403520449214},{120.790062351412,24.5402874583407},
//            {120.790151321035,24.5402551650503},{120.790265710551,24.5402013428997},{120.790303840389,24.5401582851793},{120.786579826169,24.5401259918889},{120.790456359743,24.5401690496094},
//            {120.790545329366,24.5401690496094},{120.790837658127,24.5399752898673},{120.791053727212,24.5398245878456},{120.791180826673,24.5397492368348},{120.79125708635,24.5396954146842},
//            {120.791307926134,24.5396308281035},{120.791384185811,24.5395770059529},{120.791435025596,24.5395231838023},{120.79036739012,24.5404166315021},{120.790913917804,24.5401259918889},
//            {120.791218956511,24.5399860542974},{120.791320636081,24.5398999388564},{120.791371475865,24.5398891744263},{120.791460445488,24.5398353522757},{120.791523995219,24.539770765695},
//            {120.791600254896,24.5396523569637},{120.790990177481,24.5403520449214},{120.791358765919,24.54022287176},{120.791854453819,24.5398245878456},{120.789897122112,24.5404166315021},
//            {120.791295216188,24.5403735737816},{120.791396895757,24.5403412804913},{120.791536705165,24.5402982227708},{120.791600254896,24.5402659294804},{120.79076139845,24.54113784832},
//            {120.791193536619,24.5407503288357},{120.791346055973,24.5406103912442},{120.791447735542,24.5405780979538},{120.791574835003,24.5405242758033},{120.791663804626,24.540502746943},
//            {120.791066437158,24.54113784832},{120.79165109468,24.5406534489647},{120.790443649797,24.5419451805788},{120.791041017265,24.5422896423425},{120.788232119168,24.5455943223886},
//            {120.787914370514,24.5457557888404},{120.789566663513,24.5409656174381},{120.789693762974,24.5413316080621},{120.789604793351,24.5417298919764},{120.788994715936,24.5411163194597}
//
//             ,
//
//            {120.810513597843,24.5473539267016},
//            {120.812184124335,24.5459774154598},{120.812368832956,24.5459037559954},{120.812393279685,24.5460165470503},{120.812550825273,24.5463157886246},{120.812599718731,24.546380240656},
//            {120.81300988052,24.5463963536638},{120.813096802224,24.5464446926874},{120.813343985819,24.5464193722465},{120.813544992259,24.5463549202151},{120.813773161731,24.5462398273019},
//            {120.813721551969,24.5461523566879},{120.813960586654,24.5459244727198},{120.812974568578,24.5462306198688},{120.812879497965,24.5459958303259},{120.813352134729,24.5458024742318},
//            {120.812534527453,24.5458416058223},{120.812580704608,24.5458784355545},{120.812768129532,24.5457242110508},{120.812738250196,24.5456574571612},{120.812865916449,24.545673570169},
//            {120.812974568578,24.5456275330037},{120.812947405546,24.5455607791141},{120.813096802224,24.5455722884054},{120.813316822786,24.5454664029253},{120.813292376057,24.5454180639017},
//            {120.813536843349,24.5453582155869},{120.813669942208,24.5452914616972},{120.813637346569,24.5452408208154},{120.813803041066,24.545220104091},{120.813995898596,24.5451257279022},
//            {120.812333521014,24.5455009307992},{120.812442173143,24.5454594973505},{120.812678491525,24.545332895146},{120.812969135972,24.5453536118703},{120.81320273805,24.5452638393981},
//            {120.813409177097,24.5451809725006},{120.813634630265,24.5449277680915},{120.813987749687,24.5448080714618},{120.812863200145,24.5451809725006},{120.81331953909,24.5450221442804},
//            {120.811964103773,24.5453259895712},{120.812214003671,24.545254631965},{120.812189556942,24.5451786706423},{120.81252366224,24.5450635777291},{120.812485633995,24.5451579539179},
//            {120.812501931814,24.5450129368473},{120.812702938254,24.5450958037448},{120.812681207828,24.544923164375},{120.812765413229,24.5449507866742},{120.812996299004,24.5448932402176},
//            {120.81321088696,24.5448103733201},{120.813322255393,24.5447597324383},{120.813379297761,24.5448172788949},{120.813517829226,24.5447159971313},{120.813449921645,24.5446101116511},
//            {120.813653644388,24.5446860729739},{120.813686240027,24.5447390157139},{120.813770445427,24.5446377339503},{120.81369710524,24.5445249428954},{120.813944288835,24.544582489352},
//            {120.814063806177,24.5445801874937},{120.81397416817,24.5443960388326},{120.814001331203,24.5441957771637},{120.811754948423,24.5448863346428},{120.811700622359,24.5448241844697},
//            {120.81186088425,24.5447896565957},{120.812325372104,24.5447965621705},{120.812366116652,24.5447344119974},{120.812637746976,24.54470448784},{120.812594286125,24.5446216209425},
//            {120.812534527453,24.5446400358086},{120.812575272002,24.5445111317458},{120.812553541576,24.5443546053839},{120.812510080724,24.5443569072421},{120.81312668156,24.5445249428954},
//            {120.813080504405,24.5444144536987},{120.813197305444,24.5442763422029},{120.813368432548,24.5442257013211},{120.813129397863,24.5442233994628},{120.812947405546,24.5442556254785},
//            {120.812765413229,24.5442970589273},{120.813205454354,24.5441428344236},{120.813191872837,24.5440024210695},{120.813189156534,24.5439448746129},{120.812550825273,24.5442141920298},
//            {120.812626881763,24.5441911734471},{120.812529094847,24.5440392508017},{120.813132114166,24.5437584240936},{120.813061490282,24.5436617460465},{120.813216319567,24.5437998575423},
//            {120.813615616143,24.5437077832117},{120.813618332446,24.5437630278101},{120.813971451867,24.5436548404717},{120.814169742004,24.5435857847238},{120.812387847078,24.5440829861087},
//            {120.812154245,24.5441014009749},{120.81194508965,24.5441129102662},{120.811684324539,24.5441428344236},{120.811543076771,24.5442233994628},{120.811496899616,24.5441658530062},
//            {120.811521346345,24.5443338886595},{120.811621849565,24.5444789057301},{120.811722352785,24.5446285265173}
//    };


    private double[][] datalist = {

            {1811.47375504711, 9936.78698979592}, {2264.5397039031, 10074.6262755102}, {3544.94347240915, 9858.02168367347}, {3702.53162853297, 10025.3979591837}, {3830.57200538358, 10035.243622449},
            {3929.06460296097, 9877.71301020408}, {2944.13862718708, 9779.25637755102}, {3830.57200538358, 9730.02806122449}, {3712.38088829071, 9710.33673469388}, {4391.9798115747, 9769.41071428571},
            {4362.43203230148, 9651.26275510204}, {5406.4535666218, 8942.375}, {5524.64468371467, 9040.83163265306}, {5583.7402422611, 8952.22066326531}, {5475.39838492598, 8873.45535714286},
            {5859.51951547779, 8322.09821428571}, {6371.68102288022, 7583.67346938776}, {6411.07806191117, 7760.89540816327}, {6351.98250336474, 7504.90816326531}, {6411.07806191117, 7613.21045918367},
            {6706.55585464334, 6638.48979591837}, {6824.7469717362, 6126.51530612245}, {6992.18438761777, 5299.47959183673}, {6095.90174966353, 4541.36352040816}, {5652.68506056528, 4935.19005102041},
            {5268.56393001346, 5496.39285714286}, {4874.5935397039, 6244.66326530612}, {5170.07133243607, 6441.57653061224}, {5475.39838492598, 6668.02678571429}, {5504.94616419919, 6874.78571428571},
            {5613.28802153432, 6933.85969387755}, {5258.71467025572, 7219.38392857143}, {5367.05652759085, 7298.14923469388}, {4982.93539703903, 7652.5931122449}, {5081.42799461642, 7741.20408163265},
            {5751.17765814266, 8194.10459183673}, {3840.42126514132, 9336.20153061224}, {3958.61238223419, 9375.58418367347}, {3702.53162853297, 9267.2818877551}, {3377.50605652759, 9060.52295918367},
            {1949.36339165545, 9493.73214285714}, {3475.99865410498, 8263.02423469388}, {4126.04979811575, 7367.06887755102}, {4263.93943472409, 7170.1556122449}, {4776.10094212651, 6362.8112244898},
            {5081.42799461642, 6559.72448979592}, {4598.81426648721, 7376.91454081633}, {4441.22611036339, 7563.98214285714}, {4145.74831763122, 8085.80229591837}, {4342.733512786, 8351.63520408163},
            {4194.99461641992, 8617.4681122449}, {3948.76312247645, 8942.375}, {3899.51682368775, 10596.4464285714}, {4263.93943472409, 11364.4081632653}, {4382.13055181696, 11315.1798469388},
            {5958.01211305518, 11128.112244898}, {5958.01211305518, 10537.3724489796}, {5869.36877523553, 10566.9094387755}, {6283.03768506057, 10497.9897959184}, {5721.62987886945, 9454.34948979592},
            {5859.51951547779, 9434.65816326531}, {6076.20323014805, 9277.12755102041}, {6184.54508748318, 8962.06632653061}, {6223.94212651413, 8696.23341836735}, {6273.18842530283, 8371.32653061224},
            {6529.26917900404, 7879.04336734694}, {6558.81695827725, 7593.51913265306}, {6548.96769851952, 8725.77040816327}, {6509.57065948856, 9011.29464285714}, {6509.57065948856, 9513.42346938776},
            {6588.36473755047, 9700.49107142857}, {6460.32436069987, 9877.71301020408}, {2648.66083445491, 9700.49107142857}, {3219.91790040377, 8725.77040816327}, {4500.32166890983, 8942.375},
            {4707.15612382234, 8725.77040816327}, {5229.1668909825, 6303.73724489796}, {5376.90578734859, 6431.73086734694}, {5445.85060565276, 6175.74362244898}, {5317.81022880215, 6116.66964285714},
            {5386.75504710633, 6087.13265306122}, {5475.39838492598, 6037.90433673469}, {5504.94616419919, 5998.52168367347}, {2619.1130551817, 5968.98469387755}, {5623.13728129206, 6008.36734693878},
            {5692.08209959623, 6008.36734693878}, {5918.61507402423, 5831.14540816327}, {6086.05248990579, 5693.30612244898}, {6184.54508748318, 5624.38647959184}, {6243.64064602961, 5575.15816326531},
            {6283.03768506057, 5516.08418367347}, {6342.133243607, 5466.85586734694}, {6381.53028263795, 5417.62755102041}, {5554.19246298789, 6234.81760204082}, {5977.71063257066, 5968.98469387755},
            {6214.09286675639, 5840.99107142857}, {6292.8869448183, 5762.22576530612}, {6332.28398384926, 5752.38010204082}, {6401.22880215343, 5703.15178571429}, {6450.47510094213, 5644.07780612245},
            {6509.57065948856, 5535.77551020408}, {6036.80619111709, 6175.74362244898}, {6322.43472409152, 6057.59566326531}, {6706.55585464334, 5693.30612244898}, {5189.76985195155, 6234.81760204082},
            {6273.18842530283, 6195.43494897959}, {6351.98250336474, 6165.89795918367}, {6460.32436069987, 6126.51530612245}, {6509.57065948856, 6096.97831632653}, {5859.51951547779, 6894.47704081633},
            {6194.39434724092, 6540.03316326531}, {6312.58546433378, 6412.03954081633}, {6391.37954239569, 6382.50255102041}, {6489.87213997308, 6333.27423469388}, {6558.81695827725, 6313.58290816327},
            {6095.90174966353, 6894.47704081633}, {6548.96769851952, 6451.42219387755}, {5613.28802153432, 7632.90178571429}, {6076.20323014805, 7947.96301020408}, {3899.51682368775, 10970.5816326531},
            {3653.28532974428, 11118.2665816327}, {4933.68909825034, 6736.94642857143}, {5032.18169582773, 7071.69897959184}, {4963.23687752355, 7435.98852040816}, {4490.47240915209, 6874.78571428571}

            ,

            {22460.5333655706, 1667.02309913378}, {22603.668762089, 1734.39557266602}, {22622.6131528046, 1631.23147256978}, {22744.6992263056, 1357.53079884504}, {22782.5880077369, 1298.57988450433},
            {23100.4327852998, 1283.84215591915}, {23167.7906189555, 1239.62897016362}, {23359.3394584139, 1262.78825794033}, {23515.1044487427, 1321.73917228104}, {23691.918762089, 1427.00866217517},
            {23651.9250483559, 1507.01347449471}, {23837.1590909091, 1715.44706448508}, {23073.0686653772, 1435.4302213667}, {22999.3960348162, 1650.17998075072}, {23365.6542553192, 1827.03272377286},
            {22732.0696324952, 1791.24109720885}, {22767.8534816248, 1757.55486044273}, {22913.0938104449, 1898.61597690087}, {22889.9395551257, 1959.67228103946}, {22988.8713733075, 1944.93455245428},
            {23073.0686653772, 1987.04234841193}, {23052.0193423598, 2048.09865255053}, {23167.7906189555, 2037.57170356112}, {23338.2901353965, 2134.41963426372}, {23319.3457446809, 2178.63282001925},
            {23508.7896518375, 2233.3729547642}, {23611.9313346228, 2294.42925890279}, {23586.6721470019, 2340.74783445621}, {23715.0730174081, 2359.69634263715}, {23864.5232108317, 2446.01732435034},
            {22576.3046421663, 2102.83878729548}, {22660.501934236, 2140.73580365736}, {22843.6310444874, 2256.5322425409}, {23068.8588007737, 2237.58373435996}, {23249.8829787234, 2319.69393647738},
            {23409.8578336557, 2395.48796920116}, {23584.5672147002, 2627.08084696824}, {23858.2084139265, 2736.56111645813}, {22986.7664410058, 2395.48796920116}, {23340.3950676983, 2540.75986525505},
            {22290.0338491296, 2262.84841193455}, {22483.6876208897, 2328.11549566891}, {22464.7432301741, 2397.59335899904}, {22723.6499032882, 2502.86284889317}, {22694.1808510638, 2416.54186717998},
            {22706.8104448743, 2549.18142444658}, {22862.5754352031, 2473.38739172281}, {22845.7359767892, 2631.291626564}, {22910.9888781431, 2606.02694898941}, {23089.9081237911, 2658.66169393648},
            {23256.1977756286, 2734.45572666025}, {23342.5, 2780.77430221367}, {23386.7035783366, 2728.1395572666}, {23494.0551257253, 2820.77670837344}, {23441.4318181818, 2917.62463907603},
            {23599.3017408124, 2848.14677574591}, {23624.5609284333, 2799.72281039461}, {23689.8138297872, 2892.35996150144}, {23632.9806576402, 2995.52406159769}, {23824.5294970986, 2942.88931665063},
            {23917.1465183752, 2944.99470644851}, {23847.6837524178, 3113.42589027911}, {23868.7330754352, 3296.5948026949}, {22127.9540618956, 2664.97786333013}, {22085.8554158607, 2721.82338787295},
            {22210.0464216634, 2753.40423484119}, {22569.9898452611, 2747.08806544755}, {22601.5638297872, 2803.93358999038}, {22812.0570599613, 2831.30365736285}, {22778.3781431335, 2907.09769008662},
            {22732.0696324952, 2890.25457170356}, {22763.6436170213, 3008.15640038499}, {22746.8041586073, 3151.322906641}, {22713.1252417795, 3149.21751684312}, {23190.9448742747, 2995.52406159769},
            {23155.1610251451, 3096.58277189605}, {23245.6731141199, 3222.90615976901}, {23378.2838491296, 3269.22473532243}, {23193.0498065764, 3271.33012512031}, {23052.0193423598, 3241.85466794995},
            {22910.9888781431, 3203.95765158807}, {23251.9879110251, 3345.0187680462}, {23241.4632495164, 3473.44754571704}, {23239.3583172147, 3526.0822906641}, {22744.6992263056, 3279.75168431184},
            {22803.6373307544, 3300.80558229066}, {22727.8597678917, 3439.76130895091}, {23195.1547388781, 3696.61886429259}, {23140.4264990329, 3785.04523580366}, {23260.4076402321, 3658.7218479307},
            {23569.832688588, 3742.93743984601}, {23571.9376208897, 3692.40808469682}, {23845.5788201161, 3791.36140519731}, {23999.2388781431, 3854.52309913378}, {22618.4032882012, 3399.75890279115},
            {22437.3791102515, 3382.91578440808}, {22275.2993230174, 3372.38883541867}, {22073.2258220503, 3345.0187680462}, {21963.7693423598, 3271.33012512031}, {21927.9854932302, 3323.96487006737},
            {21946.9298839458, 3170.27141482194}, {22024.8123791103, 3037.63185755534}, {22102.6948742747, 2900.78152069297}
    };


    private ArrayList<double[]> DrawPointsList = new ArrayList<>();
    {
    } // 宣告 路徑清單

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~GPS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private LocationManager locationManager;
    private static final int MinTime = 1000;//更新時間
    private static final float MinDistance = 1;//移動多少 M 才會監聽
    int GPSon = 0;
    //END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~GPS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ImageView[] marker_Move_dot;
    ImageView[] marker_Move_dot_array = new ImageView[25]; // Test Array

    int marker_Move_dot_num = 0;
    int tileView_Nooff = 1;
    private ImageView marker_Move;//作為全域使用  removeMarker

    private TileView tileView;

//    新地圖左上角經緯24.547794 120.783198
//    新地圖右下角經緯24.533581 120.816701
//    public static final double SOUTH_EAST_LONGITUDE =   120.8167;
//    public static final double SOUTH_EAST_LATITUDE = 24.5478; //南 東
//    public static final double NORTH_WEST_LONGITUDE =  120.7832;//經度     //0.012904468412943
//    public static final double NORTH_WEST_LATITUDE =24.5336; //北  西 緯度   //0.0117471872931833

//    public static final double SOUTH_EAST_LONGITUDE =   120.8167;
//    public static final double SOUTH_EAST_LATITUDE = 24.5336; //南 東
//    public static final double NORTH_WEST_LONGITUDE =  120.7832;//經度     //0.012904468412943
//    public static final double NORTH_WEST_LATITUDE =24.5478; //北  西 緯度   //0.0117471872931833

    public static final double SOUTH_EAST_LONGITUDE = 120.8167;
    public static final double SOUTH_EAST_LATITUDE = 24.533648; //南 東
    public static final double NORTH_WEST_LONGITUDE = 120.7832;//經度     //0.012904468412943
    public static final double NORTH_WEST_LATITUDE = 24.547866; //北  西 緯度   //0.0117471872931833

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // multiple references
        tileView = new TileView(this);

        // let the image explode
        // tileView.setScaleLimits(0, 2);
        tileView.setScaleLimits(0, 2);//放大大小

        // size of original image at 100% mScale

        // size and geolocation
        //tileView.setSize(25704, 11839);
        tileView.setSize(25960, 12088);

        // we won't use a downsample here, so color it similarly to tiles
        //tileView.setBackgroundColor( 0xFFe7e7e7 );

        tileView.addDetailLevel(0.0125f, "tiles/04/img%d_%d.png");
        tileView.addDetailLevel(0.2500f, "tiles/03/img%d_%d.png");
        tileView.addDetailLevel(0.5000f, "tiles/02/img%d_%d.png");
        tileView.addDetailLevel(1.0000f, "tiles/01/img%d_%d.png");
        //tileView.addDetailLevel(1.0000f, "tiles/map/phi-500000-%d_%d.jpg");

        // markers should align to the coordinate along the horizontal center and vertical bottom
        tileView.setMarkerAnchorPoints(-0.5f, -1.0f);

        //tileView.defineBounds(NORTH_WEST_LONGITUDE, NORTH_WEST_LATITUDE, SOUTH_EAST_LONGITUDE, SOUTH_EAST_LATITUDE); //%%%%%%%%%%%%%%%%%%%%

        // get metrics for programmatic DP
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // get the default paint and style it.  the same effect could be achieved by passing a custom Paint instnace
        Paint paint = tileView.getDefaultPathPaint();

        // add markers for all the points
//        for (double[] point : points) {
//            // any view will do...
//            //marker = new ImageView(this);
//            ImageView marker = new ImageView(this);
//            // save the coordinate for centering and callout positioning
//            marker.setTag(point);
//            // give it a standard marker icon - this indicator points down and is centered, so we'll use appropriate anchors
//
//            //marker.setImageResource(Math.random() < 0.75 ? R.drawable.map_marker_normal : R.drawable.map_marker_featured);//random 隨機
//            marker.setImageResource(R.drawable.map_marker_red_f);//random 隨機
//
//            // on tap show further information about the area indicated
//            // this could be done using a OnClickListener, which is a little more "snappy", since
//            // MarkerTapListener uses GestureDetector.onSingleTapConfirmed, which has a delay of 300ms to
//            // confirm it's not the start of a double-tap. But this would consume the touch event and
//            // interrupt dragging
//            tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
//            // add it to the view tree
//            tileView.addMarker(marker, point[0], point[1], null, null);
//
//        }

        // let's start off framed to the center of all points
        // 開啟至中間的位置
//        double x = 0;
//        double y = 0;
//        for (double[] point : points) {
//            x = x + point[0];
//            y = y + point[1];
//        }
//        int size = points.size();
//        x = x / size;
//        y = y / size;
//        frameTo(x, y);


        // dress up the path effects and draw it between some points
        // 陰影效果  (耗能)
//        paint.setShadowLayer(
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, metrics),
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics),
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics),
//                0x66000000
//        );

        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, metrics));  //  線寬
//        paint.setPathEffect(
//                new CornerPathEffect(
//                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, metrics)
//                )
//        ); // 線的彎曲幅度
        //paint.setPathEffect(null);  // 不做任何效果

        paint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0)); // 畫虛線
        //paint.setColor();

        //tileView.drawPath(points.subList(2, 5), null);//畫線

        // set mScale to 0, but keep scaleToFit true, so it'll be as small as possible but still match the container
        tileView.setScale(0);

        // let's use 0-1 positioning...
        // tileView.defineBounds(0, 0, 1, 1);

        // frame to center
        // frameTo(0.5, 0.5);

        // render while panning
        tileView.setShouldRenderWhilePanning(true);

        // disallow going back to minimum scale while double-taping at maximum scale (for demo purpose)
        tileView.setShouldLoopScale(false);

        // tileView.setSaveEnabled( true );

        setContentView(tileView);


        //tileView.setRotation(120);//地圖選轉 150度   繼承View的功能
        //tileView.setLayoutParams(new LinearLayout.LayoutParams(500, 500));
        //tileView.setTop(50);

        //tileView.scrollTo(1980,1920);
        //ileView.scrollBy(+500,+500);
        //tileView.setX(3000);
        //tileView.setY(3000);

//        Path p1path = new Path();
//        p1path.moveTo(400,100);
//        p1path.lineTo(600,300);
//        p1path.lineTo(600, 600);
//        p1path.lineTo(400, 800);
//        p1path.lineTo(200, 600);
//        p1path.lineTo(200, 300);
//        p1path.lineTo(400, 100);
//
//        Region p1region = new Region();
//        p1region.setPath(p1path, new Region(0,0,100,100));
//
//        HotSpot map0Hotspot = new HotSpot();
//
//        map0Hotspot.set(p1region);
//
//        map0Hotspot.setTag(0);
//        tileView.addHotSpot(map0Hotspot);

        // WORK
        HotSpot hotSpot = new HotSpot();
        hotSpot.setTag( this );
        hotSpot.set( new Rect( 0, 0, 10000, 10000 ) );
        hotSpot.setHotSpotTapListener( new HotSpot.HotSpotTapListener(){
            @Override
            public void onHotSpotTap(HotSpot hotSpot, int x, int y) {
                Log.d( "HotSpotTapped", "With access through the tag API to the Activity " );
                Log.d( "HotSpotTapped", "x=" + x + ", y=" + y );

                frameTo(800, 700);


            }

        });
        tileView.addHotSpot( hotSpot );


//        Path hotSpotPath = new Path();
//        hotSpotPath.moveTo( 100, 100 );
//        hotSpotPath.lineTo( 300, 300 );
//        hotSpotPath.lineTo( 100, 600 );
//        hotSpotPath.lineTo( 100, 100 );
//        hotSpotPath.close();
//
//        Rect hotSpotClipRect = new Rect( 100, 100, 400, 700 );
//        Region hotSpotClip = new Region( hotSpotClipRect );
//
//        Region hotSpotRegion = new Region();
//        hotSpotRegion.setPath( hotSpotPath, hotSpotClip );
//
//        HotSpot hotSpot = new HotSpot( hotSpotRegion );
//
//
//        hotSpot.setTag( "I'm a HotSpot" );
//        tileView.addHotSpot( hotSpot );

//
//        Path largePath = new Path();
//        largePath.addCircle(500,400,500, Path.Direction.CW);
//
//        Region p2region = new Region();
//        p2region.setPath(largePath, new Region(0,0,1000,1000));
//        HotSpot map1Hotspot = new HotSpot();
//        map1Hotspot.set(p2region);
//        map1Hotspot.setTag(1);
//        tileView.addHotSpot(map1Hotspot);


//        tileView.addHotSpot( points.subList( 1, 5 ), new HotSpot.HotSpotTapListener() {
//            @Override
//            public void onHotSpotTap( HotSpot hotSpot, int x, int y ) {
//                Log.d("TileView", "HotSpot tapped");
//            }
//        } );

        //GPS 服務檢查
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            //請求權限的對話框
        } else {
            locationStart();
            GPSon = 1;
        }

        // locationStart();

//        tileView.defineBounds(
//                NORTH_WEST_LONGITUDE,
//                NORTH_WEST_LATITUDE,
//                SOUTH_EAST_LONGITUDE,
//                SOUTH_EAST_LATITUDE
//        );//變成GPS座標

        //~~~~~~~~~~~~~~取得傳遞過來的資料~~~~~~~~~~~~~~
        Intent intent = this.getIntent();
        StartString = intent.getStringExtra("startstring");
        EndString = intent.getStringExtra("endstring");

        AfterSplitStartString = StartString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");
        AfterSplitEndString = EndString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");

        //    跨校區 測試
        if (Integer.parseInt(AfterSplitStartString[0]) < 125 && Integer.parseInt(AfterSplitEndString[0]) < 125) { //只有八甲
            String contentStr = ("maptestb.txt");//讀ASSETS~~~~~~~
            loadfiletoArray(contentStr);
            Plusflag = 0;
            dijkstraMin();   //  取得最短的組合
            vs = StartPointMin;
            dijkstraT();
        } else if (Integer.parseInt(AfterSplitStartString[0]) > 125 && Integer.parseInt(AfterSplitEndString[0]) > 125) {  //只有二坪

            String contentStr = ("maptesta.txt");//讀ASSETS~~~~~~~!! 用二坪的檔案
            loadfiletoArray(contentStr);
            Plusflag = 1;
            dijkstraMin();   //  取得最短的組合
            vs = StartPointMin;
            dijkstraT();
        } else { // 跨校區
            if (Integer.parseInt(AfterSplitStartString[0]) < 125) { // 八甲開始
                Doflag = 1; //  跨區旗標 = 1 終點圖示改變    (目前未使用
                Plusflag = 0;
                String contentStr = ("maptestb.txt");
                loadfiletoArray(contentStr);
                AfterSplitEndString = new String[0]; // 清空陣列
                AfterSplitEndString = new String[1];
                AfterSplitEndString[0] = String.valueOf(123); //校門口
                dijkstraMin();   //  取得最短的組合
                vs = StartPointMin;
                dijkstraT();
                AfterSplitEndString = EndString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");//恢復
                Doflag = 2; //  跨區旗標 = 2 起點圖示改變
                Plusflag = 1;
//            DrawPointsList.add(new double[]{ 120.79289624,24.538329});
//            DrawPointsList.add(new double[]{120.795621, 24.539129});
//            DrawPointsList.add(new double[]{ 120.797069,24.541462});
//            DrawPointsList.add(new double[]{120.799379, 24.543649});
//            DrawPointsList.add(new double[]{120.801997, 24.545474});
//            DrawPointsList.add(new double[]{120.804915, 24.545952});
//            DrawPointsList.add(new double[]{120.808638,24.545415});
//            DrawPointsList.add(new double[]{120.811181,24.547328});

                ListStFlag = DrawPointsList.size(); //   八甲校區已經畫了幾點

                contentStr = ("maptesta.txt");
                loadfiletoArray(contentStr);
                AfterSplitStartString = new String[0]; // 清空陣列
                AfterSplitStartString = new String[1];
                AfterSplitStartString[0] = String.valueOf(200); //校門口
                dijkstraMin();   //  取得最短的組合
                vs = StartPointMin;
                dijkstraT();
                AfterSplitStartString = StartString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");//恢復


            } else {  // 二坪開始

                Doflag = 2; //  跨區旗標 = 2 起點圖示改變
                Plusflag = 1;

                String contentStr = ("maptesta.txt");
                loadfiletoArray(contentStr);

                AfterSplitEndString = new String[0]; // 清空陣列
                AfterSplitEndString = new String[1];
                AfterSplitEndString[0] = String.valueOf(200); //校門口
                dijkstraMin();   //  取得最短的組合
                vs = StartPointMin;
                dijkstraT();
                AfterSplitEndString = EndString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");//恢復


                Doflag = 1; //  跨區旗標 = 1 終點圖示改變
                Plusflag = 0;

                contentStr = ("maptestb.txt");
                loadfiletoArray(contentStr);
                AfterSplitStartString = new String[0]; // 清空陣列
                AfterSplitStartString = new String[1];
                AfterSplitStartString[0] = String.valueOf(123); //校門口
                dijkstraMin();   //  取得最短的組合
                vs = StartPointMin;
                dijkstraT();
                AfterSplitStartString = StartString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");//恢復
            }
        }
        //   跨校區 測試

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~畫線 & 標記~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        int pointnum = 0;// 迴圈計次
        for (double[] point : DrawPointsList) {  //建立標記圖示
            // any view will do...
            //marker = new ImageView(this);
            ImageView marker = new ImageView(this);
            // save the coordinate for centering and callout positioning
            marker.setTag(point);
            // give it a standard marker icon - this indicator points down and is centered, so we'll use appropriate anchors

            //marker.setImageResource(Math.random() < 0.75 ? R.drawable.map_marker_normal : R.drawable.map_marker_featured);//random 隨機
            //random 隨機
            if (pointnum == 0) {  // 標記起點
                marker.setImageResource(R.drawable.map_marker_green_f);
            } else if (pointnum == DrawPointsList.size() - 1) {  // 標記終點
                marker.setImageResource(R.drawable.map_marker_red_f);
            } else {
                marker.setImageResource(R.drawable.map_marker_123);
            }

//            Drawable d = getResources().getDrawable(R.drawable.map_marker_green_f);
//            int h = d.getIntrinsicHeight();
//            int w = d.getIntrinsicWidth();

            // marker.getHeight(); //TTTTTTTTTTTTT
            // Toast.makeText(this,  " 高 " + String.valueOf(h) , Toast.LENGTH_SHORT).show(); // 距離

            // on tap show further information about the area indicated
            // this could be done using a OnClickListener, which is a little more "snappy", since
            // MarkerTapListener uses GestureDetector.onSingleTapConfirmed, which has a delay of 300ms to
            // confirm it's not the start of a double-tap. But this would consume the touch event and
            // interrupt dragging
            tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
            // add it to the view tree

            tileView.addMarker(marker, point[0], point[1], null, null);
            pointnum++;
        }

        if (ListStFlag != 0) { // 跨區時 分開價點測試
            // tileView.drawPath(DrawPointsList.subList( 0 , ListStFlag ), null);//  點數  畫線

            //tileView.drawPath(DrawPointsList.subList( ListStFlag , pointnum ), null);//  點數  畫線


            tileView.drawPath(DrawPointsList.subList(0, pointnum), null);//  點數  畫線
        } else {
            // DrawPointsList.size() 清單數
            tileView.drawPath(DrawPointsList.subList(0, pointnum), null);//  點數  畫線
        }
        //END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~畫線 & 標記~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //  tileView.removePath();

        //  原 單校區 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//        String contentStr = ("maptest.txt");//讀ASSETS~~~~~~~
//        loadfiletoArray(contentStr);
//        dijkstraMin();   //  取得最短的組合
//        vs =  StartPointMin;
//        dijkstraT();
        //  原 單校區 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //tileView.setRotation(150);//地圖選轉 150度   繼承View的功能
        //tileView.setRotation(90.0f);

        //frameTo(120.815 ,24.534);
        frameTo(datalist[Integer.parseInt(AfterSplitStartString[0])][0], datalist[Integer.parseInt(AfterSplitStartString[0])][1]); // 移到起始點附近


        // RAM 回收  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        mMatrix = new int[0][0];

        // timer.schedule(new MyTimerTask(), 1000, 1000);  //long delay, long period
    }

    public class MyTimerTask extends TimerTask {
        public void run() {
            // do something (cut)

            // and at the end show info
            MapMainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(JSONMainActivity .this, String.valueOf(aa), Toast.LENGTH_LONG).show();

                    tileView.setRotation(10);//地圖選轉


                }
            });
            // Toast.makeText(this,  String.valueOf(ra) , Toast.LENGTH_SHORT).show(); // 距離
        }
    }

    //  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~location~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void stopGPS() {
        if (locationManager != null) {
            Log.d("LocationActivity", "onStop()");
            // update 止
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        } else {

        }
    }

    private void locationStart() {
        Log.d("debug", "locationStart()");
        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MinTime, MinDistance, this);// 對GPS做一些設定

            Log.d("debug", "gpsEnable, startActivity");
        } else {
            Log.d("debug", "gpsEnabled");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);//重複的動作
    }

    private void locationStart_Old(){
        Log.d("debug","locationStart()");
        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);

           // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MinTime, MinDistance, this);// 對GPS做一些設定

            Log.d("debug", "gpsEnable, startActivity");
        } else {
            Log.d("debug", "gpsEnabled");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);//重複的動作
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
           // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");
                //Toast toast1 = Toast.makeText(this, "GPS 啟動", Toast.LENGTH_SHORT);
                locationStart();
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");

                Toast toast = Toast.makeText(this, "LocationProvider.AVAILABLE", Toast.LENGTH_SHORT);
                toast.show();

                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");

                Toast toast1 = Toast.makeText(this, "LocationProvider.OUT_OF_SERVICE", Toast.LENGTH_SHORT);
                toast1.show();

                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");

                Toast toast2 = Toast.makeText(this, "LocationProvider.TEMPORARILY_UNAVAILABLE", Toast.LENGTH_SHORT);
                toast2.show();
                break;
        }
    }

   // gps
    private double gps2d(double lat_a, double lng_a, double lat_b, double lng_b)
    {
        double d = 0;
        lat_a=lat_a*Math.PI/180;
        lng_a=lng_a*Math.PI/180;
        lat_b=lat_b*Math.PI/180;
        lng_b=lng_b*Math.PI/180;

        d=Math.sin(lat_a)*Math.sin(lat_b)+Math.cos(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
        d=Math.sqrt(1-d*d);
        d=Math.cos(lat_b)*Math.sin(lng_b-lng_a)/d;
        d=Math.asin(d)*180/Math.PI;
//d = Math.round(d*10000);
        return d;
    }


    double lastLongitude, lasttLatitude;

    // 該方法接收 a 點與 b 點, a 點為物件位置, b 點為目標位置
    public double GetAngle(double ax, double ay,double bx,double by)
    {
        // 這邊需要過濾掉位置相同的問題
        if ( ax == bx && ay >= by ) return 0;

        bx -= ax;
        by -= ay;
        //double angle = Math.cos(-by / bx) * (180 / Math.PI);


        double DRoation = Math.atan2(by,bx);
        double WRotation = DRoation/Math.PI*180;


        //return (bx < ax ? -angle : angle);

        return (WRotation);
    }

    @Override
    public void onLocationChanged(Location location) {  //GPS更新監聽

    if(tileView_Nooff == 1){ // 檢查 tileview 啟動

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~DOT~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        double[]  point3 = {location.getLongitude() ,location.getLatitude()};

       //tileView.removeMarker(marker_Move);

        // 移除 imageView Array
        //tileView.removeMarker(marker_Move_dot[X]);
//        for (int q = 0; q < marker_Move_dot.length ; q++) {
//            tileView.removeMarker(marker_Move_dot[q]);
//        }

//        marker_Move_dot[marker_Move_dot_num] = new ImageView(this);
//
//        marker_Move_dot[marker_Move_dot_num].setTag(point3);
//
//        marker_Move_dot[marker_Move_dot_num].setImageResource(R.drawable.dot);
//        //marker_Move.setRotation(); //旋轉 TEST
//
//        tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
//
//
//        tileView.addMarker(marker_Move_dot[marker_Move_dot_num], point3[0], point3[1], null, null);  // 使GPS座標設定Marker的位置
//
//
//        marker_Move_dot_num = marker_Move_dot_num ++;


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~DOT~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        marker_Move_dot_array[marker_Move_dot_num] = new ImageView(this);

        marker_Move_dot_array[marker_Move_dot_num].setTag(point3);

        marker_Move_dot_array[marker_Move_dot_num].setImageResource(R.drawable.dot);
        //marker_Move.setRotation(); //旋轉 TEST

        tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);


        tileView.addMarker(marker_Move_dot_array[marker_Move_dot_num], point3[0], point3[1], null, null);  // 使GPS座標設定Marker的位置


        marker_Move_dot_num = marker_Move_dot_num ++;

        if( marker_Move_dot_num == 26){
            marker_Move_dot_num = 0;
        }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~DOT~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //ImageView imageView = new ImageView( this );
        //imageView.setImageResource( R.drawable.push_pin );
        //getTileView().addMarker( imageView,  0.25, 0.25, null, null );
//        tileView.defineBounds(
//                NORTH_WEST_LONGITUDE,
//                NORTH_WEST_LATITUDE,
//                SOUTH_EAST_LONGITUDE,
//                SOUTH_EAST_LATITUDE
//        );   //設定座標

        double[]  point2 = {location.getLongitude() ,location.getLatitude()};

        tileView.removeMarker(marker_Move);

        marker_Move = new ImageView(this);
        // save the coordinate for centering and callout positioning

        marker_Move.setTag(point2);
        // give it a standard marker icon - this indicator points down and is centered, so we'll use appropriate anchors


        //~~~~~~~~~~ ~~~~~~ ~~~~~~     test   ~~~~~~ ~~~~~~        ~~~~~~
        //marker_Move.setImageResource(R.drawable.map_marker_green_f);
        marker_Move.setImageResource(R.drawable.map_min);

        //~~~~~~~~~~ ~~~~~~ ~~~~~~     test   ~~~~~~ ~~~~~~        ~~~~~~
        //marker_Move.setRotation(   (int)GetAngle( lasttLatitude,lastLongitude,location.getLatitude(),location.getLatitude()) ); //旋轉 TEST
        //~~~~~~~~~~ ~~~~~~ ~~~~~~     test   ~~~~~~ ~~~~~~        ~~~~~~

        // on tap show further information about the area indicated
        // this could be done using a OnClickListener, which is a little more "snappy", since
        // MarkerTapListener uses GestureDetector.onSingleTapConfirmed, which has a delay of 300ms to
        // confirm it's not the start of a double-tap. But this would consume the touch event and
        // interrupt dragging

        //tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);  //    !不需要監聽事件

        // add it to the view tree
        //tileView.addMarker(marker2, 120.816 ,24.536, null, null);
        tileView.addMarker(marker_Move, point2[0], point2[1], null, null);// 使GPS座標設定Marker的位置
        Toast.makeText(this, "GPS : " +  String.valueOf( location.getLongitude()) +" , "+  String.valueOf(location.getLatitude())  , Toast.LENGTH_SHORT).show(); // 距離
       // String.valueOf(GetAngle( lasttLatitude,lastLongitude,location.getLatitude(),location.getLatitude()));
        Toast.makeText(this,  String.valueOf(" & " + String.valueOf(GetAngle( lasttLatitude,lastLongitude,location.getLatitude(),location.getLatitude())))  , Toast.LENGTH_SHORT).show(); // 方向
    //   gps2d(lasttLatitude, lastLongitude,location.getLatitude(),location.getLatitude()); // 角度方法2

        //~~~~~~~~~~ ~~~~~~ ~~~~~~     test   ~~~~~~ ~~~~~~        ~~~~~~
        //marker_Move.setRotation( (int)gps2d(lasttLatitude, lastLongitude,location.getLatitude(),location.getLatitude()) ); //旋轉 TEST
        marker_Move.setRotation( (int)GetAngle(lasttLatitude, lastLongitude,location.getLatitude(),location.getLatitude()) ); //旋轉 TEST

        //~~~~~~~~~~ ~~~~~~ ~~~~~~     test   ~~~~~~ ~~~~~~        ~~~~~~

       // Toast.makeText(this,  String.valueOf(" & 2 " + String.valueOf(  gps2d(lasttLatitude, lastLongitude,location.getLatitude(),location.getLatitude())))  , Toast.LENGTH_SHORT).show(); // 方向


        lastLongitude = location.getLongitude();
        lasttLatitude = location.getLatitude();

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~RUIN~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//        double x = 0;
//        double y = 0;
//        for (double[] point : points) {
//            x = x + point[0];
//            y = y + point[1];
//        }
//        int size = points.size();
//        x = x / size;
//        y = y / size;
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~RUIN~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        } // NO OFF
}

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private MarkerLayout.MarkerTapListener markerTapListener = new MarkerLayout.MarkerTapListener() {  // Marker 監聽

        @Override
        public void onMarkerTap(View view, int x, int y) {
            // get reference to the TileView
            TileView tileView = getTileView();
            // we saved the coordinate in the marker's tag
            double[] position = (double[]) view.getTag();
            // lets center the screen to that coordinate
            tileView.slideToAndCenter(position[0], position[1]);//移動
            // create a simple callout
            SampleCallout callout = new SampleCallout(view.getContext());
            // add it to the view tree at the same position and offset as the marker that invoked it
            tileView.addCallout(callout, position[0], position[1], -0.5f, -1.0f);
            // a little sugar
            callout.transitionIn();
            // stub out some text

//            callout.setTitle("MAP CALLOUT");
//            callout.setSubtitle("Info window at coordinate:\n" + position[1] + ", " + position[0]);

            callout.setTitle("Info");
            callout.setSubtitle("位置 : " + position[1] + ", " + position[0]);

            //~~~~~~~~~~~~~~~~ 開啟室內 Activity
            Intent intent = new Intent();
            //intent.setClass(HomeMainActivity.this,ShowValue.class);
            intent.setClass(MapMainActivity.this,MapCallMainActivity.class);
            startActivity(intent);
            //~~~~~~~~~~~~~~~~ 開啟室內 Activity
        }
    };

    // a list of points to demonstrate markers and paths
    private ArrayList<double[]> points = new ArrayList<>();
    {
        points.add(new double[]{120.815 ,24.534});
        points.add(new double[]{120.814 ,24.532});
        points.add(new double[]{120.813 ,24.533});
        points.add(new double[]{120.816 ,24.531});
        points.add(new double[]{120.817 ,24.530});
        points.add(new double[]{120.818 ,24.529});
    }


    public TileView getTileView(){
        return tileView;
    }

    public void frameTo( final double x, final double y ) {
        getTileView().post( new Runnable() {
            @Override
            public void run() {
                getTileView().scrollToAndCenter( x, y );
            }
        });
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   Dijkstra  & F ile I/O~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /*
     * Dijkstra最短路徑。
     * 即，統計圖中"頂點vs"到其它各個頂點的最短路徑。
     *
     * 參數說明：
     *       vs -- 起始頂點(start vertex)。即計算"頂點vs"到其它頂點的最短路徑。
     *     prev -- 前驅頂點陣列。即，prev[i]的值是"頂點vs"到"頂點i"的最短路徑所經歷的全部頂點中，位於"頂點i"之前的那個頂點。
     *     dist -- 長度陣列。即，dist[i]是"頂點vs"到"頂點i"的最短路徑的長度。
     */
    // dijkstraT(終點)
    private void  dijkstraT(){

//        for (int i = 0; i < mVexs.length; i++) { // 9999 換成 INF = 無限大
//            mVexs[i]= i ;
//        }


        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~已做過~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//        for (int i = 0; i < mVexs.length; i++) { // 9999 換成 INF = 無限大
//            for (int j = 0; j < mVexs.length; j++) {
//                mMatrix[i][j]= Loaddata[i][j];
//                if(Loaddata[i][j]==9999){
//                    mMatrix[i][j]=INF;
//                }
//            }
//        }
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~已做過~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //vs=3;
        // vs = Integer.valueOf(txtScreen.getText().toString());
        // flag[i]=true表示"頂點vs"到"頂點i"的最短路徑已成功獲取
        boolean[] flag = new boolean[mVexs.length];
        // 初始化
        for (int i = 0; i < mVexs.length; i++) {
            parent[i] = -1;//~~~
            flag[i] = false;          // 頂點i的最短路徑還沒獲取到。
            //prev[i] = 0;              // 頂點i的前驅頂點為0。
            dist[i] = mMatrix[vs][i];  // 頂點i的最短路徑為"頂點vs"到"頂點i"的權。
        }
        // 對"頂點vs"自身進行初始化
        flag[vs] = true;
        dist[vs] = 0;
        // 遍歷mVexs.length-1次；每次找出一個頂點的最短路徑。
        int k=0;
        for (int i = 1; i < mVexs.length; i++) {
            // 尋找當前最小的路徑；
            // 即，在未獲取最短路徑的頂點中，找到離vs最近的頂點(k)。
            int min = INF;
            for (int j = 0; j < mVexs.length; j++) {
                if (flag[j]==false && dist[j]<min) {
                    min = dist[j];
                    k = j;
                }
            }
            // 標記"頂點k"為已經獲取到最短路徑
            flag[k] = true;
            // 修正當前最短路徑和前驅頂點
            // 即，當已經"頂點k的最短路徑"之後，更新"未獲取最短路徑的頂點的最短路徑和前驅頂點"。
            for (int j = 0; j < mVexs.length; j++) {
                int tmp = (mMatrix[k][j]==INF ? INF : (min + mMatrix[k][j]));
                if (flag[j]==false && (tmp<dist[j]) ) {
                    dist[j] = tmp;
                    //prev[j] = k;
                    parent[j] = k;//~~~
                }
            }
        }
//        for (int i = 0; i < mVexs.length; i++) {
//            displaytext +=  mVexs[vs] + " to " + mVexs[i] + "=" + dist[i];
//            displaytext += " ( " + vs;
//            printPath(parent, i);
//             displaytext +=  " ) ";
//             displaytext += "\n";
//
//        }
        // txtScreen.setText(displaytext);//顯示結果
//        int EndPointTemp = Integer.parseInt(EndString)-1;//CCCCCCCCCCC
//        int StartPointTemp Integer.parseInt(StartString)-1;
//
        int StartPointTemp = StartPointMin;
        int EndPointTemp = EndPointMin;

//        DrawPointsList.add(new double[]{datalist[StartPointTemp][0] ,datalist[StartPointTemp][1]});//加入起點座標
//        printPath(parent, EndPointTemp);//加入路徑座標
//        DrawPointsList.add(new double[]{datalist[EndPointTemp][0] ,datalist[EndPointTemp][1]});//加入終點座標

        if(Plusflag == 1){ // 二坪用
            DrawPointsList.add(new double[]{datalist[StartPointTemp+125][0] ,datalist[StartPointTemp+125][1]});//加入起點座標
            printPath(parent, EndPointTemp);//加入路徑座標

//            DrawPointsList.add(new double[]{24.538329, 120.792896});
//            DrawPointsList.add(new double[]{24.539129, 120.795621});
//            DrawPointsList.add(new double[]{24.541462, 120.797069});
//            DrawPointsList.add(new double[]{24.543649, 120.799379});
//            DrawPointsList.add(new double[]{24.545474, 120.801997});
//            DrawPointsList.add(new double[]{24.545952, 120.804915});
//            DrawPointsList.add(new double[]{24.545415, 120.808638});
//            DrawPointsList.add(new double[]{24.547328, 120.811181});

            DrawPointsList.add(new double[]{datalist[EndPointTemp+125+1][0] ,datalist[EndPointTemp+125][1]});//加入終點座標
        }
        else{
            DrawPointsList.add(new double[]{datalist[StartPointTemp][0] ,datalist[StartPointTemp][1]});//加入起點座標
            printPath(parent, EndPointTemp);//加入路徑座標
            DrawPointsList.add(new double[]{datalist[EndPointTemp][0] ,datalist[EndPointTemp][1]});//加入終點座標
        }

        Toast.makeText(this,  String.valueOf(dist[EndPointTemp]) , Toast.LENGTH_SHORT).show(); // 距離


        for (int r = 0;r< 227; r++) {
            points4.add(new double[]{datalist[r][0] ,datalist[r][1]});
        }
        for (double[] point : points4) {  //建立標記圖示
            // any view will do...
            //marker = new ImageView(this);

            ImageView marker = new ImageView(this);
            // save the coordinate for centering and callout positioning
            marker.setTag(point);
            // give it a standard marker icon - this indicator points down and is centered, so we'll use appropriate anchors

            //marker.setImageResource(Math.random() < 0.75 ? R.drawable.map_marker_normal : R.drawable.map_marker_featured);//random 隨機
            //random 隨機

                marker.setImageResource(R.drawable.map_marker_123);


            // on tap show further information about the area indicated
            // this could be done using a OnClickListener, which is a little more "snappy", since
            // MarkerTapListener uses GestureDetector.onSingleTapConfirmed, which has a delay of 300ms to
            // confirm it's not the start of a double-tap. But this would consume the touch event and
            // interrupt dragging
            tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
            // add it to the view tree

            tileView.addMarker(marker, point[0], point[1], null, null);
        }
    }

    private ArrayList<double[]> points4 = new ArrayList<>();//測試 所有點
    {

    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    void printPath(int parent[], int j) { // 路徑
        // Base Case : If j is source
        if (parent[j]==-1) {
            //  displaytext +=" > "+ j  ;
            // Toast.makeText(this,  String.valueOf(j) , Toast.LENGTH_SHORT).show();
           // DrawPointsList.add(new double[]{datalist[j][0] ,datalist[j][1]});
            if(Plusflag == 1){ // 二坪用
                DrawPointsList.add(new double[]{datalist[j+125][0] ,datalist[j+125][1]});
            }
            else{
                DrawPointsList.add(new double[]{datalist[j][0] ,datalist[j][1]});
            }
            return;
        }
        printPath(parent, parent[j]);

        //  displaytext +=" > "+ j  ;
        if(Plusflag == 1){ // 二坪用
            DrawPointsList.add(new double[]{datalist[j+125][0] ,datalist[j+125][1]});
        }
        else{
            DrawPointsList.add(new double[]{datalist[j][0] ,datalist[j][1]});
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~loadFile~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public String loadfiletoArray(String fStartString){
        String result=null;
        try
        {
            InputStream in=this.getResources().getAssets().open(fStartString);
            int ch=0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((ch=in.read())!=-1)
            {
                baos.write(ch);
            }
            byte[] buff=baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff,"UTF-8");//AN
            String[] AfterSplit =  result.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");// C

            int  ArraySum =  (int)Math.sqrt(AfterSplit.length);  //  開根號 及 型別轉換

            // ~~~~~~~~~~~~~~~~~~設定各種大小~~~~~~~~~~~~~~~~~~~~
           mVexs = new int[ArraySum];// 純標記
            
           mMatrix = new int[mVexs.length][mVexs.length];
           prev = new int[mVexs.length];
           dist = new int[mVexs.length];
           parent = new int[mVexs.length];

            //~~~~~~~~~~~~~~~~~~ 一維轉二維陣列 ~~~~~~~~~~~~~~~~~~~~
            int NumSplit = 0;
            for (int i = 0; i< ArraySum ; i++){
                for (int j = 0 ; j < ArraySum ; j++){
                    int temp= Integer.parseInt(AfterSplit[NumSplit]);
                    if(temp == 9999){
                        mMatrix[i][j] = INF;
                    }
                    else{
                        mMatrix[i][j] = temp;
                    }
                    NumSplit++;

                }
            }
            result=result.replaceAll("\\r\\n","\n");
        }
        catch(Exception e)
        {
            Toast.makeText(this, "你已經GG了！", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    //END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   Dijkstra  & F ile I/O~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void   dijkstraMin(){ // 多點最短路徑
        int[][] PointNumTemp = new int[ AfterSplitStartString.length ][ AfterSplitEndString.length ]; // 個路徑的距離紀錄
        int vsTemp; //  起點

//        for (int i = 0; i < mVexs.length; i++) { // 9999 換成 INF = 無限大
//            for (int j = 0; j < mVexs.length; j++) {
//                mMatrix[i][j]= Loaddata[i][j];
//                if(Loaddata[i][j]==9999){
//                    mMatrix[i][j]=INF;
//                }
//            }
//        }

        for (int PointNum = 0; PointNum <  AfterSplitStartString.length ; PointNum++) {

            vsTemp =  Integer.parseInt(AfterSplitStartString[PointNum]) -1;  //  設定起點  點的起始為1
            if(Plusflag == 1){ // 二坪用
                vsTemp = vsTemp - 125;
            }

            // flag[i]=true表示"頂點vsTemp"到"頂點i"的最短路徑已成功獲取
            boolean[] flag = new boolean[mVexs.length];
            // 初始化
            for (int i = 0; i < mVexs.length; i++) {
                parent[i] = -1;//~~~
                flag[i] = false;          // 頂點i的最短路徑還沒獲取到。
                //prev[i] = 0;              // 頂點i的前驅頂點為0。
                dist[i] = mMatrix[vsTemp][i];  // 頂點i的最短路徑為"頂點vsTemp"到"頂點i"的權。
            }
            // 對"頂點vsTemp"自身進行初始化
            flag[vsTemp] = true;
            dist[vsTemp] = 0;
            // 遍歷mVexs.length-1次；每次找出一個頂點的最短路徑。
            int k = 0;
            for (int i = 1; i < mVexs.length; i++) {
                // 尋找當前最小的路徑；
                // 即，在未獲取最短路徑的頂點中，找到離vsTemp最近的頂點(k)。
                int min = INF;
                for (int j = 0; j < mVexs.length; j++) {
                    if (flag[j] == false && dist[j] < min) {
                        min = dist[j];
                        k = j;
                    }
                }
                // 標記"頂點k"為已經獲取到最短路徑
                flag[k] = true;
                // 修正當前最短路徑和前驅頂點
                // 即，當已經"頂點k的最短路徑"之後，更新"未獲取最短路徑的頂點的最短路徑和前驅頂點"。
                for (int j = 0; j < mVexs.length; j++) {
                    int tmp = (mMatrix[k][j] == INF ? INF : (min + mMatrix[k][j]));
                    if (flag[j] == false && (tmp < dist[j])) {
                        dist[j] = tmp;
                        //prev[j] = k;
                        parent[j] = k;//~~~
                    }
                }
            }

            //  存入各點之間的距離大小
            for (int Nend = 0; Nend <  AfterSplitEndString.length ; Nend++) {
                //dist[Nend] 距離
                if(Plusflag == 1){ // 二坪用 -125  (125)
                    PointNumTemp[PointNum][Nend] = dist[Integer.parseInt(AfterSplitEndString[Nend])-125];
                }
                else{
                    PointNumTemp[PointNum][Nend] = dist[Integer.parseInt(AfterSplitEndString[Nend]) -1]; // 給終點 取出最短路徑
                }
            }
        }

        // 最小位置
        int MinxPoint=0 ,  MinyPoint=0;
        for (int qx = 0;qx < AfterSplitStartString.length ; qx++) {
            for (int qy = 0; qy < AfterSplitEndString.length ; qy++) {
                if (PointNumTemp[MinxPoint][MinyPoint] > PointNumTemp[qx][qy]) {
                    MinxPoint = qx;
                    MinyPoint = qy;
                }

            }
        }

        // 取出對應的點
//        StartPointMin = Integer.parseInt(AfterSplitStartString[MinxPoint]) -1; // 最近短距離的 起點
//        EndPointMin   = Integer.parseInt(AfterSplitEndString[MinyPoint])   -1;   // 最近短距離的 終點

        if(Plusflag == 1){ // 二坪用
            // 設定最短的起點+終點   從別的Activity取得原始的值
            StartPointMin = Integer.parseInt(AfterSplitStartString[MinxPoint]) -1 -125; // 最近短距離的 起點
            EndPointMin   = Integer.parseInt(AfterSplitEndString[MinyPoint])   -1 -125;   // 最近短距離的 終點
        }
        else{
            StartPointMin = Integer.parseInt(AfterSplitStartString[MinxPoint]) -1; // 最近短距離的 起點
            EndPointMin   = Integer.parseInt(AfterSplitEndString[MinyPoint])   -1;   // 最近短距離的 終點
        }
    }

    public void onPause() { //暫停
        super.onPause();
        tileView.pause();
        tileView_Nooff = 0;
      //  stopLocationUpdates(); // 停止GPS更新
        Toast.makeText(this,  "stop", Toast.LENGTH_SHORT).show(); // 距離
//        if(GPSon == 1){
//            stopGPS();
//        }
        //GPS 服務檢查
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
//        }
//        else{
//            stopGPS();
//        }
    }

    @Override
    public void onResume() { //
        super.onResume();
        tileView.resume();
        tileView_Nooff = 1;

        Toast.makeText(this,  "play", Toast.LENGTH_SHORT).show(); // 距離
//        if(GPSon == 1){
//            locationStart();
//        }
        //GPS 服務檢查
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
//        }
//        else{
//            locationStart();
//        }
    }

    @Override
    public void onDestroy() { //銷毀
        super.onDestroy();
        tileView.destroy();
        tileView = null;
    }

}