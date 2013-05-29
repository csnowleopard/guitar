package overlayGraph;

import overlayGraph.util.SiteInfo;

public class VisualizationMediawikiTestDriver {
	
	public static void main(String[] args) {
        final String nodeClickedURL= "http://localhost/mediawiki/mediawiki/index.php/Main_Page";
        final String ripName = "GUITAR-Mediawiki";
        
        final SiteInfo siteInfo= new SiteInfo(ripName + ".xml");

        new Simulator(nodeClickedURL, nodeClickedURL, ripName, siteInfo);
    }
}
