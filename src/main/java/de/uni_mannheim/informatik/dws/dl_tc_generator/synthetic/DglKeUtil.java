package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.TripleDataSetMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults.DEFAULT_DGL_KE_DIR;

/**
 * Functionality to facilitate using DGL-KE for KG embeddings.
 */
public class DglKeUtil {


    private static final Logger LOGGER = LoggerFactory.getLogger(DglKeUtil.class);

    public static void generateDglKeGraphDirectory(File resultsFile){

        // loop over test case collection
        for(File tcc : resultsFile.listFiles()){
            // example for tcc: "tc01"

            if(tcc.isFile()){
                continue;
            }

            for(File tcg : tcc.listFiles()){
                // example for tcg: /tc01/synthetic_ontology

                if(tcg.isFile()){
                    continue;
                }

                File dataDir = new File(new File(tcg, DEFAULT_DGL_KE_DIR), "data");
                if(dataDir.exists()){
                    LOGGER.error("Directory exists already: " + dataDir.getAbsolutePath() + "\nSkipping.");
                    continue;
                }
                File graphFile = new File(tcg, "graph.nt");
                if(graphFile.exists()){
                    TripleDataSetMemory tds = TripleDataSetMemory.parseNtFile(graphFile);
                    Map<String, Integer> entityIds = new HashMap<>();
                    int id = 0;
                    for(String entity : tds.getObjectNodes()){
                        entityIds.put(entity, id++);
                    }
                    Map<String, Integer> relationIds = new HashMap<>();
                    id = 0;
                    for(String relation : tds.getUniqueObjectTriplePredicates()){
                        relationIds.put(relation, id++);
                    }
                    dataDir.mkdirs();

                    File entitiesFile = new File(dataDir, "entities.dict");
                    File relationsFile = new File(dataDir, "relations.dict");
                    File trainFile = new File(dataDir, "train.tsv");
                    try (BufferedWriter entitiesWriter =
                                 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entitiesFile),
                                         StandardCharsets.UTF_8));
                                 BufferedWriter relationsWriter =
                                 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(relationsFile),
                                         StandardCharsets.UTF_8));
                                 BufferedWriter trainWriter =
                                 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(trainFile),
                                         StandardCharsets.UTF_8));
                    ) {
                        // writing entities file
                        for(Map.Entry<String, Integer> entry: entityIds.entrySet()){
                            entitiesWriter.write(entry.getKey() + "\t" + entry.getValue());
                            entitiesWriter.write("\n");
                        }

                        // writing relations file
                        for(Map.Entry<String, Integer> entry: relationIds.entrySet()){
                            relationsWriter.write(entry.getKey() + "\t" + entry.getValue());
                            relationsWriter.write("\n");
                        }

                        // writing train file
                        for(Triple t : tds.getAllObjectTriples()){
                            trainWriter.write(entityIds.get(t.subject) + "\t" + relationIds.get(t.predicate) +
                            "\t" + entityIds.get(t.object) + "\n");
                        }

                    } catch (FileNotFoundException fnfe){
                        LOGGER.error("File not found exception.", fnfe);
                    } catch (IOException e) {
                        LOGGER.error("IOException.", e);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        DglKeUtil.generateDglKeGraphDirectory(new File("./results/synthetic_ontology"));
    }

}
