/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.test.data;

import java.util.List;

import com.google.gson.Gson;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.RoutesCollection;

public class PlacesFragmentTestData {
	
	final public static String two_routes_two_incidents = "{  \n" + 
			"   \"result\":{  \n" + 
			"      \"Trip\":{  \n" + 
			"         \"Routes\":[  \n" + 
			"            {  \n" + 
			"               \"Summary\":{  \n" + 
			"                  \"Text\":\"Via NE Redmond Fall City Rd, WA-520 and Lake Washington Blvd E\"\n" + 
			"               },\n" + 
			"               \"CompressionPoints\":\"kguaHho_hVY?wBQe@Ba@Bs@XiEdC]Ri@TmA`@gAPE?e@J]LUNgA\\\\]LUFY?cACcAUUGw@Yo@Ci@?gBF]Cm@Co@M}Am@_Aa@]IUCi@?U?{@L_A\\\\o@\\\\qCzBi@`@o@\\\\e@T]Jo@Ha@?sBB_AJkBP_ANs@LY?YCOEw@a@YY]a@GG]o@a@_AQ{@Gi@ImAKcAKk@McAOeC?w@?q@?w@E_AGw@KiAIe@Oi@]{@qAsBU]Ok@Qm@U{AWoAQs@UiAOoACQ?UEO?MOCe@GYCUGcAQg@MiBW_BUoAMiEa@k@Ca@Cm@Gg@EY?Y?e@Da@NYHQJWJQPYTYXOXYd@U`@a@~@cAhCsB`FkBzEa@fAYj@Yh@]n@{@pAi@z@QR]^YR]X]Pa@NGBQDUFYBWBg@Be@?s@?_A?o@?i@?]?s@Bi@DCl@E\\\\CB?\\\\C\\\\?HGRChA?v@?jA?|B?hECz@?bA?z@?h@?n@?h@B`ABh@B\\\\?XFv@Hh@Bj@Fl@Jv@H\\\\FXFXDTFXFNFTPv@F\\\\Ld@\\\\xANv@HTJd@J`@H\\\\F\\\\FXH`@Fj@Fn@BVBn@Dd@Bf@?d@?d@C~@?b@E\\\\C`@C\\\\C\\\\K~@MlAO|AQ`Ba@`ECd@Ij@G`@KtAId@a@zD]rD]tCYlCOrBa@nDEf@SlBa@rDQdAUzBC\\\\Cj@Gh@Mv@CXC`@Mv@C`@Gd@C\\\\C\\\\Mz@YtAKj@CJUtAUr@Ol@GXM\\\\K\\\\MTK\\\\MXGPKRMXYj@O\\\\U\\\\Q\\\\KRQTKPQNKTGBILSXQNw@v@_A~@o@r@a@`@yEbFUTKJIJGHGJMLGJUXyC|CyCzCcBdBSTw@r@yA`BY\\\\QJ]b@]ZGHUTwBvBQNKPa@`@U\\\\UXSX]d@k@~@KPQ\\\\]r@]l@Uj@]z@]z@]~@Sl@Ur@KXMTKNMP]h@]b@qAz@gCfAUPQJGLGJIPGXCVCTCT?T?R?PBTBNBTBNBLHPFJFJHLFJJHjBfAXPhCnA\\\\TJFLHJJtAxAFFFLHFBLfAdDTn@\\\\dBPr@Br@`@vDJhC?zBCxF?hGBdB?fBCpGCrHClE?dEBdEC|ABfD?bA?h@?hABbAB\\\\?TDXBv@BJBf@?VBXFr@Dn@Fd@NbALdATnARlAXbAHb@Nh@Jd@b@lARn@P`@FN`@z@Pb@`@t@\\\\j@v@fAX\\\\TTd@h@`@f@d@`@dAv@\\\\Xh@\\\\TJ`@TrDfBjFvBhAd@z@`@tAn@`MnFd@NbBr@fA`@hAXbATbAPhANbAJhAHl@BH?h@Bz@?r@?~@Cv@Cv@Iv@Gz@KbAQv@On@Ml@Uz@Sf@QjE}Aj@Uz@]lG_C~IcDLGrAa@z@Y|@Uz@Kz@Qz@Gz@Cz@Cz@?\\\\?\\\\Bz@Bz@Jz@Lz@N\\\\HXJJBj@Pd@Nn@Tl@Xv@`@r@`@r@f@\\\\VRPr@j@r@l@h@n@r@v@\\\\`@TXXXBF\\\\`@r@lA\\\\h@h@bA\\\\v@`CzEzB|ElEdJ\\\\t@FLBFHPvDdI\\\\r@h@fA~A|CnC`GlAdCLX\\\\p@Vr@Th@Tn@Pd@d@pAX~@VhA^pARnATlAF\\\\PfANlALbAJlAHn@BNB`@Bz@F`AH`BBbABpAB`B?tAGjBCpAa@hIIlAcAlTGlAEp@?b@CrABtA?|AHrBXlH?h@FjBF`B?`B?v@?j@C`BCd@Cn@Gz@M~@Gv@Mz@Kn@yC|SM~@i@nDs@xEa@tCGn@E\\\\GXC`@C`@C`@ETCXC`@CXCX?XCR?XC\\\\?b@Ch@?`@EP?`@?`B?`BDbBBpCBbA?`BFrC?p@?n@?JCn@?v@Cp@G`AM~@K~@Q~@CTMd@CTCFCHUz@Sl@ELCFYh@Sj@U`@Yd@QTa@n@Y\\\\]\\\\WXYNg@\\\\mBhAw@`@g@Xe@Xo@`@i@Xs@d@s@n@a@\\\\YRa@`@i@n@]\\\\]`@o@v@UXq@~@CBcDlEuCdEs@hA}AlBk@r@W`@c@r@a@n@KVQXa@z@Uf@e@bA]~@]~@YdAUbAShAQz@ObAI\\\\CFC\\\\?HM~@Gv@C\\\\CRGhAMjBUtEGfAGxAUvCGbACd@EhA?d@C~@?bA?dABfAHdAFfALbABXFn@TpAX`BRbATdAT~@v@nDF`@r@bDr@tCF\\\\RnAXpAnBzI`@dBHf@Nd@d@hCXfAX~@Tz@Tj@Xr@Rd@LTJXn@jA`@r@pEnHbAbBHNNTT\\\\Th@Tf@Rd@Xr@Xz@Xz@Tv@Nz@BJPv@PdAJfATnBFjABj@?FB\\\\?hABjAChA?fAChAGfAMxAOdCa@|EMxAKdAa@fFQvBQdBkA`PIp@CLa@rFYnD]hECd@]|D]tEqC|[Cv@sF|p@mAnOw@vJCPiCd[mGzv@C\\\\Gb@C\\\\E\\\\CVCTGTQpAUjAWpAg@rBOj@Ml@On@]xAQfAUpAOpAQnAGvAMrAGtAK`GYhIQdGWbLc@bOGhCKnDQnAcAxN?f@E\\\\DP?N?J?HBF?LBNFPBJDLFNFLFJPTFFBFLHNJPLPBRF\\\\?hABzBB\\\\?d@?TBPHXJXL~@p@`@XTPl@`@XNTHTBl@F\\\\HF?HBFIBKPURSLQFQFK\\\\w@DGFY?MBC?CFCHCBCFCJBL?XF`@F\\\\Bd@HH?VBTFv@Fv@?v@CX?J?LCNBLBNHLJFFTj@r@`BFXJJPHpCTNBTBdBTXBLBTFFBJ?P?JCPGXMXK`@YNKTILGJCPCJ?n@Ch@?FCLEl@a@j@s@JKHKFMJKHINGLCJCFCLCJ?L?JBF?PBPBB?l@Jn@LTFNBTBX?TCR?TCTCJCXI\\\\KJCTUz@q@PQNMTOPGRQTKPIXOl@YPMJGLGFMJKLQFSHMRw@TgAPiA`@eBJ]Pk@Na@TYPYNOTMPGNGT?`@?J?P?PCJCJELGFGLIFKBCBGBILGFGFCH?B?X?d@p@P\\\\FLFJ~@xAxBjDVj@pApBr@lAbA`BXn@`@j@rBhDXj@JJr@lAz@tArBdDtA`CxCbFT`@~@dBf@v@Xd@BDJJd@z@L?`@?`GBnABL?J?L?\\\\DpA?jFFxA?lCBjFFJ?pC?rBBjDDt@BzAC|A?bAB`@??N?rD?|F?r@\",\n" + 
			"               \"Incidents\":[  \n" + 
			"                  1076630000,\n" + 
			"                  1386748804\n" + 
			"               ],\n" + 
			"               \"Id\":\"734536538\",\n" + 
			"               \"HasClosures\":false,\n" + 
			"               \"TravelTimeMinutes\":34,\n" + 
			"               \"AbnormalityMinutes\":0,\n" + 
			"               \"UncongestedTravelTimeMinutes\":29,\n" + 
			"               \"AverageSpeed\":34.0,\n" + 
			"               \"RouteQuality\":3,\n" + 
			"               \"TrafficConsidered\":true,\n" + 
			"               \"TotalDistance\":19.6,\n" + 
			"               \"SpeedBuckets\":[  \n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"kguaHho_hVY?wBQe@Ba@Bs@XiEdC]Ri@TmA`@gAPE?e@J]LUNgA\\\\]LUFY?cACcAUUGw@Yo@Ci@?gBF]Cm@Co@M}Am@_Aa@]IUCi@?U?{@L_A\\\\o@\\\\qCzBi@`@o@\\\\e@T]Jo@Ha@?sBB_AJkBP_ANs@LY?YCOEw@a@YY]a@GG]o@a@_AQ{@Gi@ImAKcAKk@McAOeC?w@?q@?w@E_AGw@KiAIe@Oi@]{@qAsBU]Ok@Qm@U{AWoAQs@UiAOoACQ?UEO?MOCe@GYCUGcAQg@MiBW_BUoAMeBO\",\n" + 
			"                     \"startOffset\":0.0,\n" + 
			"                     \"endOffset\":2518.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"scyaHr`~gViEa@\",\n" + 
			"                     \"startOffset\":2518.0,\n" + 
			"                     \"endOffset\":4169.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":1,\n" + 
			"                     \"CompressionPoints\":\"}iyaHp_~gVnHp@\",\n" + 
			"                     \"startOffset\":4169.0,\n" + 
			"                     \"endOffset\":4868.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"m`yaHba~gVyNuA\",\n" + 
			"                     \"startOffset\":4868.0,\n" + 
			"                     \"endOffset\":4948.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"gpyaHl~}gVfXhC\",\n" + 
			"                     \"startOffset\":4948.0,\n" + 
			"                     \"endOffset\":6257.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"_wxaHvb~gV_h@_F\",\n" + 
			"                     \"startOffset\":6257.0,\n" + 
			"                     \"endOffset\":7068.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"_`zaHv{}gVfaAhJ\",\n" + 
			"                     \"startOffset\":7068.0,\n" + 
			"                     \"endOffset\":7835.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"w}waH`g~gVgjBgQ\",\n" + 
			"                     \"startOffset\":7835.0,\n" + 
			"                     \"endOffset\":10666.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"_i{aHxt}gVnlDp\\\\\",\n" + 
			"                     \"startOffset\":10666.0,\n" + 
			"                     \"endOffset\":12247.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"o{uaHjr~gVwwG{n@\",\n" + 
			"                     \"startOffset\":12247.0,\n" + 
			"                     \"endOffset\":14538.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":1,\n" + 
			"                     \"CompressionPoints\":\"gt~aHnb}gVfeMllA\",\n" + 
			"                     \"startOffset\":14538.0,\n" + 
			"                     \"endOffset\":14836.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":0,\n" + 
			"                     \"CompressionPoints\":\"_npaH|o_hV_~Ui|B\",\n" + 
			"                     \"startOffset\":14836.0,\n" + 
			"                     \"endOffset\":15194.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"_mgbHrr{gVfdd@viE\",\n" + 
			"                     \"startOffset\":15194.0,\n" + 
			"                     \"endOffset\":15914.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"wgbaHj}ahVic{@_gI\",\n" + 
			"                     \"startOffset\":15914.0,\n" + 
			"                     \"endOffset\":16972.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"al~bHjuwgVth`BvqO\",\n" + 
			"                     \"startOffset\":16972.0,\n" + 
			"                     \"endOffset\":18885.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"kb}_HbhhhVyl|CyyY\",\n" + 
			"                     \"startOffset\":18885.0,\n" + 
			"                     \"endOffset\":20535.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"epzdHhmmgVvv}Frlj@\",\n" + 
			"                     \"startOffset\":20535.0,\n" + 
			"                     \"endOffset\":23864.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"mx{|G|zxhVwc{KggeA\",\n" + 
			"                     \"startOffset\":23864.0,\n" + 
			"                     \"endOffset\":24680.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"e}wiHtrrfVr}ySdupB\",\n" + 
			"                     \"startOffset\":24680.0,\n" + 
			"                     \"endOffset\":25575.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"q~|tGzhdjVy|u`@m|vD\",\n" + 
			"                     \"startOffset\":25575.0,\n" + 
			"                     \"endOffset\":26666.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"k|svHlkldVrtySrspBk@Ca@Cm@Gg@EY?Y?e@Da@NYHQJWJQPYTYXOXYd@U`@a@~@cAhCsB`FkBzEa@fAYj@Yh@]n@{@pAi@z@QR]^YR]X]Pa@NGBQDUFYBWBg@Be@?s@?_A?o@?i@?]?s@Bi@DCl@E\\\\CB?\\\\C\\\\?HGRChA?v@?jA?|B?hECz@?bA?z@?h@?n@?h@B`ABh@B\\\\?XFv@Hh@Bj@Fl@Jv@H\\\\FXFXDTFXFNFTPv@F\\\\Ld@\\\\xANv@HTJd@J`@H\\\\F\\\\FXH`@Fj@Fn@BVBn@Dd@Bf@?d@?d@C~@?b@E\\\\C`@C\\\\C\\\\K~@MlAO|AQ`Ba@`ECd@Ij@G`@KtAId@a@zD]rD]tCYlCOrBa@nDEf@SlBa@rDQdAUzBC\\\\Cj@Gh@Mv@CXC`@Mv@C`@Gd@C\\\\C\\\\Mz@YtAKj@CJUtAUr@Ol@GXM\\\\K\\\\MTK\\\\MXGPKRMXYj@O\\\\U\\\\Q\\\\KRQTKPQNKTGBILSXQNw@v@_A~@o@r@a@`@yEbFUTKJIJGHGJMLGJUXyC|CyCzCcBdBSTw@r@yA`BY\\\\QJ]b@]ZGHUTwBvBQNKPa@`@U\\\\UXSX]d@k@~@KPQ\\\\]r@]l@Uj@]z@]z@]~@Sl@Ur@KXMTKNMP]h@]b@qAz@gCfAUPQJGLGJIPGXCVCTCT?T?R?PBTBNBTBNBLHPFJFJHLFJJHjBfAXPhCnA\\\\TJFLHJJtAxAFFFLHFBLfAdDTn@\\\\dBPr@Br@`@vDJhC?zBCxF?hGBdB?fBCpGCrHClE?dEBdEC|ABfD?bA?h@?hABbAB\\\\?TDXBv@BJBf@?VBXFr@Dn@Fd@NbALdATnARlAXbAHb@Nh@Jd@b@lARn@P`@FN`@z@Pb@`@t@\\\\j@v@fAX\\\\TTd@h@`@f@d@`@dAv@\\\\Xh@\\\\TJ`@TrDfBjFvBhAd@z@`@tAn@`MnFd@NbBr@fA`@hAXbATbAPhANbAJhAHl@BH?h@Bz@?r@?~@Cv@Cv@Iv@Gz@KbAQv@On@Ml@Uz@Sf@QjE}Aj@Uz@]lG_C~IcDLGrAa@z@Y|@Uz@Kz@Qz@Gz@Cz@Cz@?\\\\?\\\\Bz@Bz@Jz@Lz@N\\\\HXJJBj@Pd@Nn@Tl@Xv@`@r@`@r@f@\\\\VRPr@j@r@l@h@n@r@v@\\\\`@TXXXBF\\\\`@r@lA\\\\h@h@bA\\\\v@`CzEzB|ElEdJ\\\\t@FLBFHPvDdI\\\\r@h@fA~A|CnC`GlAdCLX\\\\p@Vr@Th@Tn@Pd@d@pAX~@VhA^pARnATlAF\\\\PfANlALbAJlAHn@BNB`@Bz@F`AH`BBbABpAB`B?tAGjBCpAa@hIIlAcAlTGlAEp@?b@CrABtA?|AHrBXlH?h@FjBF`B?`B?v@?j@C`BCd@Cn@Gz@M~@Gv@Mz@Kn@yC|SM~@i@nDs@xEa@tCGn@E\\\\GXC`@C`@C`@ETCXC`@CXCX?XCR?XC\\\\?b@Ch@?`@EP?`@?`B?`BDbBBpCBbA?`BFrC?p@?n@?JCn@?v@Cp@G`AM~@K~@Q~@CTMd@CTCFCHUz@Sl@ELCFYh@Sj@U`@Yd@QTa@n@Y\\\\]\\\\WXYNg@\\\\mBhAw@`@g@Xe@Xo@`@i@Xs@d@s@n@a@\\\\YRa@`@i@n@]\\\\]`@o@v@UXq@~@CBcDlEuCdEs@hA}AlBk@r@W`@c@r@a@n@KVQXa@z@Uf@e@bA]~@]~@YdAUbAShAQz@ObAI\\\\CFC\\\\?HM~@Gv@C\\\\CRGhAMjBUtEGfAGxAUvCGbACd@EhA?d@C~@?bA?dABfAHdAFfALbABXFn@TpAX`BRbATdAT~@v@nDF`@r@bDr@tCF\\\\RnAXpAnBzI`@dBHf@Nd@d@hCXfAX~@Tz@Tj@Xr@Rd@LTJXn@jA`@r@pEnHbAbBHNNTT\\\\Th@Tf@Rd@Xr@Xz@Xz@Tv@Nz@BJPv@PdAJfATnBFjABj@?FB\\\\?hABjAChA?fAChAGfAMxAOdCa@|EMxAKdAa@fFQvBQdBkA`PIp@CLa@rFYnD]hECd@]|D]tEqC|[Cv@sF|p@mAnOw@vJCPiCd[mGzv@C\\\\Gb@C\\\\E\\\\CVCTGTQpAUjAWpAg@rBOj@Ml@On@]xAQfAUpAOpAQnAGvAMrAGtAK`GYhIQdGWbLc@bOGhCKnDQnAcAxN?f@E\\\\DP?N?J?HBF?LBNFPBJDLFNFLFJPTFFBFLHNJPLPBRF\\\\?hABzBB\\\\?d@?TBPHXJXL~@p@`@XTPl@`@XNTHTBl@F\\\\HF?HBFIBKPURSLQFQFK\\\\w@DGFY?MBC?CFCHCBCFCJBL?XF`@F\\\\Bd@HH?VBTFv@Fv@?v@CX?J?LCNBLBNHLJFFTj@r@`BFXJJPHpCTNBTBdBTXBLBTFFBJ?P?JCPGXMXK`@YNKTILGJCPCJ?n@Ch@?FCLEl@a@j@s@JKHKFMJKHINGLCJCFCLCJ?L?JBF?PBPBB?l@Jn@LTFNBTBX?TCR?TCTCJCXI\\\\KJCTUz@q@PQNMTOPGRQTKPIXOl@YPMJGLGFMJKLQFSHMRw@TgAPiA`@eBJ]Pk@Na@TYPYNOTMPGNGT?`@?J?P?PCJCJELGFGLIFKBCBGBILGFGFCH?B?X?d@p@P\\\\FLFJ~@xAxBjDVj@pApBr@lAbA`BXn@`@j@rBhDXj@JJr@lAz@tArBdDtA`CxCbFT`@~@dBf@v@Xd@BDJJd@z@L?`@?`GBnABL?J?L?\\\\DpA?jFFxA?lCBjFFJ?pC?rBBjDDt@BzAC|A?bAB`@??N?rD?|F?r@\",\n" + 
			"                     \"startOffset\":26666.0,\n" + 
			"                     \"endOffset\":28811.0\n" + 
			"                  }\n" + 
			"               ]\n" + 
			"            },\n" + 
			"            {  \n" + 
			"               \"Summary\":{  \n" + 
			"                  \"Text\":\"Via NE Redmond Fall City Rd, WA-520 and I-90\"\n" + 
			"               },\n" + 
			"               \"CompressionPoints\":\"kguaHho_hVY?wBQe@Ba@Bs@XiEdC]Ri@TmA`@gAPE?e@J]LUNgA\\\\]LUFY?cACcAUUGw@Yo@Ci@?gBF]Cm@Co@M}Am@_Aa@]IUCi@?U?{@L_A\\\\o@\\\\qCzBi@`@o@\\\\e@T]Jo@Ha@?sBB_AJkBP_ANs@LY?YCOEw@a@YY]a@GG]o@a@_AQ{@Gi@ImAKcAKk@McAOeC?w@?q@?w@E_AGw@KiAIe@Oi@]{@qAsBU]Ok@Qm@U{AWoAQs@UiAOoACQ?UEO?MOCe@GYCUGcAQg@MiBW_BUoAMiEa@k@Ca@Cm@Gg@EY?Y?e@Da@NYHQJWJQPYTYXOXYd@U`@a@~@cAhCsB`FkBzEa@fAYj@Yh@]n@{@pAi@z@QR]^YR]X]Pa@NGBQDUFYBWBg@Be@?s@?_A?o@?i@?]?s@Bi@DCl@E\\\\CB?\\\\C\\\\?HGRChA?v@?jA?|B?hECz@?bA?z@?h@?n@?h@B`ABh@B\\\\?XFv@Hh@Bj@Fl@Jv@H\\\\FXFXDTFXFNFTPv@F\\\\Ld@\\\\xANv@HTJd@J`@H\\\\F\\\\FXH`@Fj@Fn@BVBn@Dd@Bf@?d@?d@C~@?b@E\\\\C`@C\\\\C\\\\K~@MlAO|AQ`Ba@`ECd@Ij@G`@KtAId@a@zD]rD]tCYlCOrBa@nDEf@SlBa@rDQdAUzBC\\\\Cj@Gh@Mv@CXC`@Mv@C`@Gd@C\\\\C\\\\Mz@YtAKj@CJUtAUr@Ol@GXM\\\\K\\\\MTK\\\\MXGPKRMXYj@O\\\\U\\\\Q\\\\KRQTKPQNKTGBILSXQNw@v@_A~@o@r@a@`@yEbFUTKJIJGHGJMLGJUXyC|CyCzCcBdBSTw@r@yA`BY\\\\QJ]b@]ZGHUTwBvBQNKPa@`@U\\\\UXSX]d@k@~@KPQ\\\\]r@]l@Uj@]z@]z@]~@Sl@Ur@KXMTKNMP]h@]b@qAz@gCfAUPQJGLGJIPGXCVCTCT?T?R?PBTBNBTBNBLHPFJFJHLFJJHjBfAXPhCnA\\\\TJFLHJJtAxAFFFLHFBLfAdDTn@\\\\dBPr@Br@`@vDJhC?zBCxF?hGBdB?fBCpGCrHClE?dEBdEC|ABfD?bA?h@?hABbAB\\\\?TDXBv@BJBf@?VBXFr@Dn@Fd@NbALdATnARlAXbAHb@Nh@Jd@b@lARn@P`@FN`@z@Pb@`@t@\\\\j@v@fAX\\\\TTd@h@`@f@d@`@dAv@\\\\Xh@\\\\TJ`@TrDfBjFvBhAd@z@`@tAn@`MnFd@NbBr@fA`@hAXbATbAPhANbAJhAHl@BH?h@Bz@?r@?~@Cv@Cv@Iv@Gz@KbAQv@On@Ml@Uz@Sf@QjE}Aj@Uz@]lG_C~IcDLGrAa@z@Y|@Uz@Kz@Qz@Gz@Cz@Cz@?\\\\?\\\\Bz@Bz@Jz@Lz@N\\\\HXJJBj@Pd@Nn@Tl@Xv@`@r@`@r@f@\\\\VRPr@j@r@l@h@n@r@v@z@|AT\\\\\\\\h@\\\\j@Xd@T`@Vr@n@tAPd@Rn@HTFRBPBXB`@DP?`@?|A?jB?z@EJDT?TErA?tAD\\\\?H?JBP`BBlE?z@?L?l@?d@?^?F?N?n@?F?PJlCCR?n@?h@?xABtA?D?B?F?`@?n@BrB?h@Cn@?l@BT?nB?J?lEB\\\\?P?pAD\\\\Bz@?tA?d@C`B?xAB~ABR?fB?tABjA?tA?dCBbA?~@?xA?P?`@?`@?dC?`B?z@?bBCl@ChA?|A?z@?tAClCC|AEX?`BCfACr@ClCC|A?`CC~BClA?zB?n@Bt@?n@BX?T?t@CD?pEGdB?n@EP?V?XDj@?pCFrAFT?\\\\?P?X?\\\\?\\\\?XCbA?d@?f@?V?tA?dA?X?\\\\?VCT?LCRCPEtAS~@MrBYd@GXC`@C`@?n@?|A?`A?`@Bv@?~@BbAB~@BL?dD?pA?hA?rBDlBB~A?pGFpE?`EBh@CbACbBC`DCr@?z@?\\\\BP?J?FBT?PBX?RBT?P?F?J?L?J?r@?v@?`@?j@?V?\\\\?b@?X?V?n@BxA?bA?\\\\BT?T?XCh@?\\\\?r@?fAXhCLpAFl@BD?d@?`@Cd@Gb@I`@OXM\\\\ONITSNMz@o@b@[NMTGBCHERCT?F?L?NBPHNFLFFHLNNPLTBJJXFXH\\\\B\\\\B`@?`@GnIIdFKvIKxJE~B?\\\\?f@?p@KrDJhCWjMEzBWpNQ~IMfHOzICbCGdEEzB?vB?\\\\GbFCpA?\\\\CvB?d@?j@Cp@?r@IrFCdCC`B?~C?pC?N?hCBfA?B?D?N?H?RBf@?`@?N?\\\\BX?lAHtA?`@BbAJpCDdABh@JrD?PBbAFpAD\\\\?`@Bd@BbBTpEl@fQ`@fLPfD?BB~@BbA?HDl@?H?FEtA?FCv@?`@Cr@GxAIv@Ch@C\\\\Gz@?TMz@m@nFQbBGh@Cr@Ch@En@?h@?T?P?\\\\?d@DpAFjABXLdAFd@Fv@d@tCP~@XfBPbA`@bCXbB`@bC\\\\zBRpAf@xCJr@BT\\\\|CL`CB\\\\?d@BT?d@Bn@?jA?hA?z@Cz@CJCpAMtAY`EgAxN]tEY|CCP?B]lCUbAOz@Qz@Y`AUt@q@nBYz@k@hASd@w@lAgBtC_AtAo@fA]n@cAvBUh@Yv@}Ln_@]~@i@dBmCrGyEbLUh@Qf@e@jAYz@a@lAa@tA]fAMn@Sv@o@lCyAvFa@fBYjAs@xC_AxDa@xAk@`BK`@GPY~@g@pA_A~Ba@dAQ\\\\Wv@IJ_AvBK\\\\M`@K\\\\CPINCTGXGXIXGd@Kr@UtAUhBCj@Gr@Cl@Er@?r@Cz@Bd@?h@PlCF\\\\Hn@JfAThAFd@J`@Lj@J`@P`@J\\\\Pf@Xl@Td@Vr@b@bAbAjBT\\\\d@~@JXTv@Tv@Pz@Nz@PbAJdABXHl@Bn@BNBb@?p@Bz@?v@?r@?~@CjD?r@CxAGvME~BCtEC~EGbJQxUClCClCClEM|PGtJK~RQdZGtHCtGInH?n@CvB?n@CrB?dBCnBC~B?dGEjD?pC?bACrE?JBjDDJEhC?h@?tA?jB?tAC|A?`@?F?P?L?V?f@?~@K~M?vB?BCn@?FBzB?hA?PC~@?tA?\\\\?\\\\?X?NQhCGrD?T?JCbBCp@Ej@G`@Gd@K\\\\M`@QXO\\\\QXYXa@`@gAz@iA~@w@hAGBwDlCMBeBtAw@h@i@\\\\sBxAqAz@a@^YNUPwB|AwDfCgD`Cs@d@a@\\\\e@\\\\UPKFILKJM?oAG_AFcD?uC?U?O?qA?gB?eDB_D?s@?eBB}A?eC?yI?M?BiG?}E?k@?eG?eG?_G?aGmE?BqE\",\n" + 
			"               \"Incidents\":[  \n" + 
			"                  1415160528,\n" + 
			"                  1423531432\n" + 
			"               ],\n" + 
			"               \"Id\":\"734536539\",\n" + 
			"               \"HasClosures\":false,\n" + 
			"               \"TravelTimeMinutes\":44,\n" + 
			"               \"AbnormalityMinutes\":3,\n" + 
			"               \"UncongestedTravelTimeMinutes\":34,\n" + 
			"               \"AverageSpeed\":30.0,\n" + 
			"               \"RouteQuality\":2,\n" + 
			"               \"TrafficConsidered\":true,\n" + 
			"               \"TotalDistance\":22.4,\n" + 
			"               \"SpeedBuckets\":[  \n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"kguaHho_hVY?wBQe@Ba@Bs@XiEdC]Ri@TmA`@gAPE?e@J]LUNgA\\\\]LUFY?cACcAUUGw@Yo@Ci@?gBF]Cm@Co@M}Am@_Aa@]IUCi@?U?{@L_A\\\\o@\\\\qCzBi@`@o@\\\\e@T]Jo@Ha@?sBB_AJkBP_ANs@LY?YCOEw@a@YY]a@GG]o@a@_AQ{@Gi@ImAKcAKk@McAOeC?w@?q@?w@E_AGw@KiAIe@Oi@]{@qAsBU]Ok@Qm@U{AWoAQs@UiAOoACQ?UEO?MOCe@GYCUGcAQg@MiBW_BUoAM]C\",\n" + 
			"                     \"startOffset\":0.0,\n" + 
			"                     \"endOffset\":2518.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"kayaH~`~gViEa@\",\n" + 
			"                     \"startOffset\":2518.0,\n" + 
			"                     \"endOffset\":4169.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":1,\n" + 
			"                     \"CompressionPoints\":\"ugyaH|_~gVfFd@\",\n" + 
			"                     \"startOffset\":4169.0,\n" + 
			"                     \"endOffset\":4868.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"m`yaHba~gVqLgA\",\n" + 
			"                     \"startOffset\":4868.0,\n" + 
			"                     \"endOffset\":4948.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"_nyaHz~}gVvSnB\",\n" + 
			"                     \"startOffset\":4948.0,\n" + 
			"                     \"endOffset\":6257.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"gyxaHjb~gVga@yD\",\n" + 
			"                     \"startOffset\":6257.0,\n" + 
			"                     \"endOffset\":7068.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"o{yaHp|}gV~u@jH\",\n" + 
			"                     \"startOffset\":7068.0,\n" + 
			"                     \"endOffset\":7835.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"odxaH|e~gVixAgN\",\n" + 
			"                     \"startOffset\":7835.0,\n" + 
			"                     \"endOffset\":10666.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"y}zaHtv}gVjoCrW\",\n" + 
			"                     \"startOffset\":10666.0,\n" + 
			"                     \"endOffset\":10955.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"mmvaHho~gVuhFyf@\",\n" + 
			"                     \"startOffset\":10955.0,\n" + 
			"                     \"endOffset\":11541.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":1,\n" + 
			"                     \"CompressionPoints\":\"cw}aHng}gV~xJj_A\",\n" + 
			"                     \"startOffset\":11541.0,\n" + 
			"                     \"endOffset\":12196.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"c}qaHzg_hVsbRggB\",\n" + 
			"                     \"startOffset\":12196.0,\n" + 
			"                     \"endOffset\":15682.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"w`ebHr_|gVr|]tgD\",\n" + 
			"                     \"startOffset\":15682.0,\n" + 
			"                     \"endOffset\":17281.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"ccfaHhhahVi`q@}oG\",\n" + 
			"                     \"startOffset\":17281.0,\n" + 
			"                     \"endOffset\":19568.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"mdxbHjwxgV`~oAtxL\",\n" + 
			"                     \"startOffset\":19568.0,\n" + 
			"                     \"endOffset\":21356.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"keg`H`qfhVi_bCsiU\",\n" + 
			"                     \"startOffset\":21356.0,\n" + 
			"                     \"endOffset\":21550.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"uejdHlfpgVn~rEhcc@\",\n" + 
			"                     \"startOffset\":21550.0,\n" + 
			"                     \"endOffset\":22027.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"efv}GvjthVe~uI{my@\",\n" + 
			"                     \"startOffset\":22027.0,\n" + 
			"                     \"endOffset\":23173.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"kemhHz{yfVd_jPnr}A\",\n" + 
			"                     \"startOffset\":23173.0,\n" + 
			"                     \"endOffset\":24297.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":1,\n" + 
			"                     \"CompressionPoints\":\"eebwGjoxiVoz`[w`xC\",\n" + 
			"                     \"startOffset\":24297.0,\n" + 
			"                     \"endOffset\":28658.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"u`dsHrm_eV~dll@~uvF\",\n" + 
			"                     \"startOffset\":28658.0,\n" + 
			"                     \"endOffset\":31026.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":1,\n" + 
			"                     \"CompressionPoints\":\"uzveGrdwlVkfmhAgsoK\",\n" + 
			"                     \"startOffset\":31026.0,\n" + 
			"                     \"endOffset\":31866.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":2,\n" + 
			"                     \"CompressionPoints\":\"abeoIjpf`Vht|uBnvgS\",\n" + 
			"                     \"startOffset\":31866.0,\n" + 
			"                     \"endOffset\":32358.0\n" + 
			"                  },\n" + 
			"                  {  \n" + 
			"                     \"SpeedBucketID\":3,\n" + 
			"                     \"CompressionPoints\":\"wlgxEzgotV_yphAyfpKk@Ca@Cm@Gg@EY?Y?e@Da@NYHQJWJQPYTYXOXYd@U`@a@~@cAhCsB`FkBzEa@fAYj@Yh@]n@{@pAi@z@QR]^YR]X]Pa@NGBQDUFYBWBg@Be@?s@?_A?o@?i@?]?s@Bi@DCl@E\\\\CB?\\\\C\\\\?HGRChA?v@?jA?|B?hECz@?bA?z@?h@?n@?h@B`ABh@B\\\\?XFv@Hh@Bj@Fl@Jv@H\\\\FXFXDTFXFNFTPv@F\\\\Ld@\\\\xANv@HTJd@J`@H\\\\F\\\\FXH`@Fj@Fn@BVBn@Dd@Bf@?d@?d@C~@?b@E\\\\C`@C\\\\C\\\\K~@MlAO|AQ`Ba@`ECd@Ij@G`@KtAId@a@zD]rD]tCYlCOrBa@nDEf@SlBa@rDQdAUzBC\\\\Cj@Gh@Mv@CXC`@Mv@C`@Gd@C\\\\C\\\\Mz@YtAKj@CJUtAUr@Ol@GXM\\\\K\\\\MTK\\\\MXGPKRMXYj@O\\\\U\\\\Q\\\\KRQTKPQNKTGBILSXQNw@v@_A~@o@r@a@`@yEbFUTKJIJGHGJMLGJUXyC|CyCzCcBdBSTw@r@yA`BY\\\\QJ]b@]ZGHUTwBvBQNKPa@`@U\\\\UXSX]d@k@~@KPQ\\\\]r@]l@Uj@]z@]z@]~@Sl@Ur@KXMTKNMP]h@]b@qAz@gCfAUPQJGLGJIPGXCVCTCT?T?R?PBTBNBTBNBLHPFJFJHLFJJHjBfAXPhCnA\\\\TJFLHJJtAxAFFFLHFBLfAdDTn@\\\\dBPr@Br@`@vDJhC?zBCxF?hGBdB?fBCpGCrHClE?dEBdEC|ABfD?bA?h@?hABbAB\\\\?TDXBv@BJBf@?VBXFr@Dn@Fd@NbALdATnARlAXbAHb@Nh@Jd@b@lARn@P`@FN`@z@Pb@`@t@\\\\j@v@fAX\\\\TTd@h@`@f@d@`@dAv@\\\\Xh@\\\\TJ`@TrDfBjFvBhAd@z@`@tAn@`MnFd@NbBr@fA`@hAXbATbAPhANbAJhAHl@BH?h@Bz@?r@?~@Cv@Cv@Iv@Gz@KbAQv@On@Ml@Uz@Sf@QjE}Aj@Uz@]lG_C~IcDLGrAa@z@Y|@Uz@Kz@Qz@Gz@Cz@Cz@?\\\\?\\\\Bz@Bz@Jz@Lz@N\\\\HXJJBj@Pd@Nn@Tl@Xv@`@r@`@r@f@\\\\VRPr@j@r@l@h@n@r@v@z@|AT\\\\\\\\h@\\\\j@Xd@T`@Vr@n@tAPd@Rn@HTFRBPBXB`@DP?`@?|A?jB?z@EJDT?TErA?tAD\\\\?H?JBP`BBlE?z@?L?l@?d@?^?F?N?n@?F?PJlCCR?n@?h@?xABtA?D?B?F?`@?n@BrB?h@Cn@?l@BT?nB?J?lEB\\\\?P?pAD\\\\Bz@?tA?d@C`B?xAB~ABR?fB?tABjA?tA?dCBbA?~@?xA?P?`@?`@?dC?`B?z@?bBCl@ChA?|A?z@?tAClCC|AEX?`BCfACr@ClCC|A?`CC~BClA?zB?n@Bt@?n@BX?T?t@CD?pEGdB?n@EP?V?XDj@?pCFrAFT?\\\\?P?X?\\\\?\\\\?XCbA?d@?f@?V?tA?dA?X?\\\\?VCT?LCRCPEtAS~@MrBYd@GXC`@C`@?n@?|A?`A?`@Bv@?~@BbAB~@BL?dD?pA?hA?rBDlBB~A?pGFpE?`EBh@CbACbBC`DCr@?z@?\\\\BP?J?FBT?PBX?RBT?P?F?J?L?J?r@?v@?`@?j@?V?\\\\?b@?X?V?n@BxA?bA?\\\\BT?T?XCh@?\\\\?r@?fAXhCLpAFl@BD?d@?`@Cd@Gb@I`@OXM\\\\ONITSNMz@o@b@[NMTGBCHERCT?F?L?NBPHNFLFFHLNNPLTBJJXFXH\\\\B\\\\B`@?`@GnIIdFKvIKxJE~B?\\\\?f@?p@KrDJhCWjMEzBWpNQ~IMfHOzICbCGdEEzB?vB?\\\\GbFCpA?\\\\CvB?d@?j@Cp@?r@IrFCdCC`B?~C?pC?N?hCBfA?B?D?N?H?RBf@?`@?N?\\\\BX?lAHtA?`@BbAJpCDdABh@JrD?PBbAFpAD\\\\?`@Bd@BbBTpEl@fQ`@fLPfD?BB~@BbA?HDl@?H?FEtA?FCv@?`@Cr@GxAIv@Ch@C\\\\Gz@?TMz@m@nFQbBGh@Cr@Ch@En@?h@?T?P?\\\\?d@DpAFjABXLdAFd@Fv@d@tCP~@XfBPbA`@bCXbB`@bC\\\\zBRpAf@xCJr@BT\\\\|CL`CB\\\\?d@BT?d@Bn@?jA?hA?z@Cz@CJCpAMtAY`EgAxN]tEY|CCP?B]lCUbAOz@Qz@Y`AUt@q@nBYz@k@hASd@w@lAgBtC_AtAo@fA]n@cAvBUh@Yv@}Ln_@]~@i@dBmCrGyEbLUh@Qf@e@jAYz@a@lAa@tA]fAMn@Sv@o@lCyAvFa@fBYjAs@xC_AxDa@xAk@`BK`@GPY~@g@pA_A~Ba@dAQ\\\\Wv@IJ_AvBK\\\\M`@K\\\\CPINCTGXGXIXGd@Kr@UtAUhBCj@Gr@Cl@Er@?r@Cz@Bd@?h@PlCF\\\\Hn@JfAThAFd@J`@Lj@J`@P`@J\\\\Pf@Xl@Td@Vr@b@bAbAjBT\\\\d@~@JXTv@Tv@Pz@Nz@PbAJdABXHl@Bn@BNBb@?p@Bz@?v@?r@?~@CjD?r@CxAGvME~BCtEC~EGbJQxUClCClCClEM|PGtJK~RQdZGtHCtGInH?n@CvB?n@CrB?dBCnBC~B?dGEjD?pC?bACrE?JBjDDJEhC?h@?tA?jB?tAC|A?`@?F?P?L?V?f@?~@K~M?vB?BCn@?FBzB?hA?PC~@?tA?\\\\?\\\\?X?NQhCGrD?T?JCbBCp@Ej@G`@Gd@K\\\\M`@QXO\\\\QXYXa@`@gAz@iA~@w@hAGBwDlCMBeBtAw@h@i@\\\\sBxAqAz@a@^YNUPwB|AwDfCgD`Cs@d@a@\\\\e@\\\\UPKFILKJM?oAG_AFcD?uC?U?O?qA?gB?eDB_D?s@?eBB}A?eC?yI?M?BiG?}E?k@?eG?eG?_G?aGmE?BqE\",\n" + 
			"                     \"startOffset\":32358.0,\n" + 
			"                     \"endOffset\":32454.0\n" + 
			"                  }\n" + 
			"               ]\n" + 
			"            }\n" + 
			"         ],\n" + 
			"         \"Id\":\"734536538\"\n" + 
			"      },\n" + 
			"      \"Incidents\":[  \n" + 
			"         {  \n" + 
			"            \"FullDesc\":\"Construction and bridge maintenance work on WA-520 Westbound between 92nd Avenue and Montlake Boulevard.\",\n" + 
			"            \"ShortDesc\":\"WA-520 W/B : Bridge maintenance work between 92nd Avenue and Montlake Boulevard \",\n" + 
			"            \"Id\":1076630000,\n" + 
			"            \"Version\":65,\n" + 
			"            \"Type\":1,\n" + 
			"            \"Severity\":2,\n" + 
			"            \"EventCode\":707,\n" + 
			"            \"Latitude\":47.64439,\n" + 
			"            \"Longitude\":-122.304459,\n" + 
			"            \"Impacting\":true,\n" + 
			"            \"StartTime\":\"2014-08-07T06:00:21Z\",\n" + 
			"            \"EndTime\":\"2014-08-07T12:00:21Z\",\n" + 
			"            \"IncidentParameterizedDescription\":{  \n" + 
			"               \"EventText\":\"Bridge maintenance operations\",\n" + 
			"               \"RoadName\":\"Wa-520\",\n" + 
			"               \"Direction\":\"WB\",\n" + 
			"               \"FromLocation\":\"Redmond Eastbound\",\n" + 
			"               \"ToLocation\":\"Broadway Westbound\",\n" + 
			"               \"Crossroad1\":{  \n" + 
			"                  \"tmcCodeField\":\"114N04233\",\n" + 
			"                  \"valueField\":\"92nd Avenue \"\n" + 
			"               },\n" + 
			"               \"Crossroad2\":{  \n" + 
			"                  \"tmcCodeField\":\"114N04229\",\n" + 
			"                  \"valueField\":\"Montlake Boulevard \"\n" + 
			"               },\n" + 
			"               \"Position1\":\"between\",\n" + 
			"               \"Position2\":\"and\",\n" + 
			"               \"eventCode\":707\n" + 
			"            }\n" + 
			"         },\n" + 
			"         {  \n" + 
			"            \"FullDesc\":\"One lane closed on WA-520 Westbound at Evergreen Point Floating Brg.\",\n" + 
			"            \"ShortDesc\":\"WA-520 W/B : Lane closed at Evergreen Point Floating Brg \",\n" + 
			"            \"Id\":1416844982,\n" + 
			"            \"Version\":1,\n" + 
			"            \"Type\":1,\n" + 
			"            \"Severity\":2,\n" + 
			"            \"EventCode\":701,\n" + 
			"            \"Latitude\":47.64449,\n" + 
			"            \"Longitude\":-122.285454,\n" + 
			"            \"Impacting\":false,\n" + 
			"            \"StartTime\":\"2014-08-20T04:00:38Z\",\n" + 
			"            \"EndTime\":\"2014-08-20T13:00:38Z\",\n" + 
			"            \"IncidentParameterizedDescription\":{  \n" + 
			"               \"EventText\":\"Road construction\",\n" + 
			"               \"RoadName\":\"Wa-520\",\n" + 
			"               \"Direction\":\"WB\",\n" + 
			"               \"FromLocation\":\"Redmond Eastbound\",\n" + 
			"               \"ToLocation\":\"Broadway Westbound\",\n" + 
			"               \"Crossroad1\":{  \n" + 
			"                  \"tmcCodeField\":\"114N04231\",\n" + 
			"                  \"valueField\":\"Evergreen Point Floating Brg \"\n" + 
			"               },\n" + 
			"               \"Crossroad2\":{  \n" + 
			"                  \"tmcCodeField\":null,\n" + 
			"                  \"valueField\":null\n" + 
			"               },\n" + 
			"               \"Position1\":\"at\",\n" + 
			"               \"eventCode\":701\n" + 
			"            }\n" + 
			"         },\n" + 
			"         {  \n" + 
			"            \"FullDesc\":\"Two lanes closed due to construction on I-90 Westbound between Exit 9 Bellevue Way and Exit 8 Mercer Way.\",\n" + 
			"            \"ShortDesc\":\"I-90 W/B : Lanes closed between Exit 9 Bellevue Way and Exit 8 Mercer Way \",\n" + 
			"            \"Id\":1415160528,\n" + 
			"            \"Version\":1,\n" + 
			"            \"Type\":1,\n" + 
			"            \"Severity\":3,\n" + 
			"            \"EventCode\":740,\n" + 
			"            \"Latitude\":47.57807,\n" + 
			"            \"Longitude\":-122.2073,\n" + 
			"            \"Impacting\":true,\n" + 
			"            \"StartTime\":\"2014-08-07T03:00:46Z\",\n" + 
			"            \"EndTime\":\"2014-08-07T15:00:46Z\",\n" + 
			"            \"IncidentParameterizedDescription\":{  \n" + 
			"               \"EventText\":\"Road construction. Two lanes closed\",\n" + 
			"               \"RoadName\":\"I-90\",\n" + 
			"               \"Direction\":\"WB\",\n" + 
			"               \"FromLocation\":\"Eastbound\",\n" + 
			"               \"ToLocation\":\"Westbound\",\n" + 
			"               \"Crossroad1\":{  \n" + 
			"                  \"tmcCodeField\":\"114P04113\",\n" + 
			"                  \"valueField\":\"Exit 9 Bellevue Way \"\n" + 
			"               },\n" + 
			"               \"Crossroad2\":{  \n" + 
			"                  \"tmcCodeField\":\"114P04114\",\n" + 
			"                  \"valueField\":\"Exit 8 Mercer Way \"\n" + 
			"               },\n" + 
			"               \"Position1\":\"between\",\n" + 
			"               \"Position2\":\"and\",\n" + 
			"               \"eventCode\":740\n" + 
			"            }\n" + 
			"         },\n" + 
			"         {  \n" + 
			"            \"FullDesc\":\"Traffic congestion, average speed 30mph on I-90 WB between Mercer Way/Exit 6 and I-90 North Tunnel (East)\",\n" + 
			"            \"ShortDesc\":\"Congestion, avg 30mph on I-90 WB between Mercer Way and I-90 North Tunnel (East)\",\n" + 
			"            \"Id\":1423531432,\n" + 
			"            \"Version\":1,\n" + 
			"            \"Type\":3,\n" + 
			"            \"Severity\":2,\n" + 
			"            \"EventCode\":74,\n" + 
			"            \"Latitude\":47.58989,\n" + 
			"            \"Longitude\":-122.286407,\n" + 
			"            \"Impacting\":true,\n" + 
			"            \"StartTime\":\"2014-08-06T22:08:00Z\",\n" + 
			"            \"EndTime\":\"2014-08-06T22:38:00Z\",\n" + 
			"            \"IncidentParameterizedDescription\":{  \n" + 
			"               \"EventText\":\"Traffic congestion, average speed 30mph\",\n" + 
			"               \"RoadName\":\"I-90\",\n" + 
			"               \"Direction\":\"WB\",\n" + 
			"               \"Crossroad1\":{  \n" + 
			"                  \"tmcCodeField\":\"114P04117\",\n" + 
			"                  \"valueField\":\"Mercer Way/Exit 6\"\n" + 
			"               },\n" + 
			"               \"Crossroad2\":{  \n" + 
			"                  \"tmcCodeField\":\"114P08271\",\n" + 
			"                  \"valueField\":\"I-90 North Tunnel (East)\"\n" + 
			"               },\n" + 
			"               \"Position1\":\"between\",\n" + 
			"               \"Position2\":\"and\",\n" + 
			"               \"eventCode\":74\n" + 
			"            }\n" + 
			"         }\n" + 
			"      ]\n" + 
			"   },\n" + 
			"   \"StatusId\":0,\n" + 
			"   \"StatusText\":null,\n" + 
			"   \"CreatedDate\":\"2014-08-06T22:15:17Z\",\n" + 
			"   \"ResponseId\":\"4b56cdf8-2058-432f-92e6-6008cba239ae\",\n" + 
			"   \"VersionNumber\":\"1.0\",\n" + 
			"   \"DocType\":\"FindRoute\",\n" + 
			"   \"PHSDataReceived\":0\n" + 
			"}";

	public static List<Route> getTwoRoutesWithTwoIncidents() {
		RoutesCollection collection = null;

		String json = two_routes_two_incidents;
		try {
			Gson gson = new Gson();
			collection = gson.fromJson(json, RoutesCollection.class);
			return collection.getRoutes();
		} catch (Exception e) {
		}
		return null;
	}

}
