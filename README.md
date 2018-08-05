# GraphsAndMobility
<style type="text/css">
body h1 {
	text-align: center;
	font-family: Georgia, "Times New Roman", Times, serif;
}
p {
	font-family: Georgia, "Times New Roman", Times, serif;
}
li {
	font-family: Georgia, "Times New Roman", Times, serif;
}
</style>
<title>Time-Varying Road Network</title>

<h1>Milan Time-Varying Road Network Data and Manipulation Tool</h1>
<p>Welcome! Here you can find a sample time-varying network data and the instructions for manipulating the data. The methodology is presented in &quot;<a href="generatingNetwork.pdf">Generating Time-Varying Road Network Data Using Sparse Trajectories</a>&quot; published in SSTDM Workshop at ICDM 2016. Please cite our paper when you use this data. </p> 
<h2>Instructions and The Data</h2>
<p><strong>1)</strong> You can download the road network data using the following links:</p>
<ul>
  <li><a href="http://cs.bilkent.edu.tr/~elif.eser/RoadNetworkFiltered.gdb">Real data based syntetic time-varying road network </a></li>
  <li><a href="http://cs.bilkent.edu.tr/~elif.eser/RoadNetworkPartialFiltered.gdb">Syntetic and real data hyrid time-varying road network</a></li>
</ul>
<p>Both data include a time-varying network but with differently generated time-varying weights. The data are kept in Sparksee database format. </p>
<p><strong>2)</strong> Before using the database with <a href="roadNetwork.jar">the Java API</a> for manipulation of them, you need to visit <a href="http://www.sparsity-technologies.com/#licenses">Sparksee</a> for licenses. There are free licenses for researchers and developers.</p>
<p><strong>3)</strong>After getting required license, you are ready to use the provided Java API! Here is the <a href="documentation">Documentation</a> of it. Also, you can reach the source code via <a href="https://github.com/eeser/roadNetwork/">the GitHub repository</a>.</p>
