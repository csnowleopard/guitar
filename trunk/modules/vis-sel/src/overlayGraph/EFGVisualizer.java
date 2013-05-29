package overlayGraph;

import javax.swing.JPanel;

import overlayGraph.util.SiteInfo;

public class EFGVisualizer extends JPanel {
    private static final long serialVersionUID = 1L;
    private EFGCanvas canvas;

    
    public EFGVisualizer(String initialURLofRip, String nodeClickedURL, String ripName, SiteInfo siteInfo) {
        
    	this.canvas= new EFGCanvas(initialURLofRip, nodeClickedURL, ripName, siteInfo);
        this.add(this.canvas);
        
        this.setVisible(true);
    }
    
    public EFGCanvas getCanvas() {
    	
        return this.canvas;
    }
    
    //Sets Zoom with an initial Pan
    protected void setZoom(double scale, double horOffset, double vertOffset){
    	
    	this.canvas.getCamera().setViewScale(scale); 
    	this.setPan(horOffset, vertOffset);
    }
    
    protected void setPan(double horOffset, double vertOffset){
    	
    	this.canvas.getCamera().setViewOffset(horOffset,  vertOffset);     	
    }
    
}