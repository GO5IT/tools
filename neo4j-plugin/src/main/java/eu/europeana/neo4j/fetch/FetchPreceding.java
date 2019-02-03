/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europeana.neo4j.fetch;

import eu.europeana.neo4j.utils.FamilyTherapist;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luthien
 */
@javax.ws.rs.Path("/preceding")
public class FetchPreceding {


    private static final RelationshipType ISNEXTINSEQUENCE  = DynamicRelationshipType.withName("isFakeOrder");
    private static final RelationshipType ISFAKEORDER  = DynamicRelationshipType.withName("edm:isNextInSequence");

    private GraphDatabaseService db;

    public FetchPreceding(@Context GraphDatabaseService db) {
        this.db = db;
    }

    @GET
    @javax.ws.rs.Path("/rdfAbout/{rdfAbout}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getpreceding(@PathParam("rdfAbout") String rdfAbout,
                                 @QueryParam("offset") @DefaultValue("0") int offset,
                                 @QueryParam("limit") @DefaultValue("10") int limit) {
        List<Node> precedingSiblings = new ArrayList<>();
        rdfAbout = FamilyTherapist.fixSlashes(rdfAbout);
        try ( Transaction tx = db.beginTx() ) {
            IndexManager    index      = db.index();
            Index<Node>     edmsearch2 = index.forNodes("edmsearch2");
            IndexHits<Node> hits       = edmsearch2.get("rdf_about", rdfAbout);
            Node            sibling    = hits.getSingle();
            if (sibling==null) {
                throw new IllegalArgumentException("no node found in index for rdf_about = " + rdfAbout);
            }

            // Gather all ye preceding brothers and sisters but take heed! No more than in 'limit' number shall ye come!
            TraversalDescription td = db.traversalDescription()
                    .breadthFirst()
                    .relationships(ISFAKEORDER, Direction.OUTGOING)
                    .relationships(ISNEXTINSEQUENCE, Direction.OUTGOING)
                    .uniqueness(Uniqueness.RELATIONSHIP_GLOBAL)
                    .evaluator(Evaluators.excludeStartPosition())
                    .evaluator(Evaluators.fromDepth(offset + 1))
                    .evaluator(Evaluators.toDepth(offset + limit));

            // Add to the results
            for (org.neo4j.graphdb.Path path : td.traverse(sibling)) {
                Node child = path.endNode();
                precedingSiblings.add(child);
            }

            String obj = new FamilyTherapist().siblingsToJson(precedingSiblings, "siblings");
            tx.success();
            return Response.ok().entity(obj).header(HttpHeaders.CONTENT_TYPE,
                    "application/json").build();
        }
    }
}