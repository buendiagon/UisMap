package com.buendiagon.uismap.retrofit;

import com.buendiagon.uismap.entities.RetroEdge;
import com.buendiagon.uismap.entities.RetroNode;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetDataService {

    @GET("/getNodes")
    Call<List<RetroNode>> getNodes();

    @GET("/getEdges")
    Call<List<RetroEdge>> getEdges();

    @POST("/insertNode")
    @FormUrlEncoded
    Call<RetroNode> insertNode(
            @Field("node_info") String nodeInfo,
            @Field("lat") Double lat,
            @Field("lng") Double lng
    );

    @POST("/insertEdge")
    @FormUrlEncoded
    Call<RetroEdge> insertEdge(
            @Field("from_node") Integer fromNode,
            @Field("to_node") Integer toNode,
            @Field("weight") Float weight
    );

}
