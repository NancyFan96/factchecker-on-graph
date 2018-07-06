# factchecker-on-graph

This is my undergraduate thesis project.


In modern, digital society, traditional fact checking by expert cannot keep up with the enormous volume of information that is now generated on the Web. In order to automatically find out false assertions, here we show that fact checking can be viewed as finding and evaluating certain paths between nodes on knowledge graphs. Given a statement with several claims, our system will extract triples and map them into paths in a knowledge graph by entity looking up and path querying. Then the similarity between triples and paths are evaluated. The evaluation results are used for final classification.

We proposed a novel fact checking system on graph database. The specific contributions of this paper are as follows:

1. We developed a novel system that can detect false claims in statements. Here we focus on the feasibility of automatic fact checking using natural language processing technology and knowledge graph querying and evaluation. Instead of accurate mapping and evaluation, we access our final result by bulk computation and approximation. Pruning and cache are used to improve the performance.

2. We modeled fact checking as querying semi-bidirection paths on knowledge graph. As one-direction path query reveals few relation between concept nodes, and bi-direction path query returns too many redundant results, semi-bidirection path query reaches an elegant balance between the two.

3. Our system has a good performance of on the Wikipedia test case.

KEYWORDS: Fact checking, Graph database, Knowledge graph, NLP, Path finding
