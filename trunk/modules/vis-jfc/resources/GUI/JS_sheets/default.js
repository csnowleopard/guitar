//JavaScript Document

window.onload=init;
//record the javascript codes of all nodes and lines
var nodeString = new Array();
var lineString = new Array();
//the text on the node
var nodeText = new Array()
//to record the steps, make easy to be back and forth, start from 0
var stepCount=0;
//record the number of nodes/lines generated in each step; start from 0
var nodeCount = new Array();
var lineCount = new Array();
//Record the information of each node to be displayed on the right; start from 0
var nodeInformation = new Array();
var t, infor;
var nodeId = new Array();

//Scalar information for node positions
var XSCALAR = 1.25;
var XSHIFT = 150;
var YSCALAR = 1.25;
var YSHIFT = 100;

//undo stack
var undoStack = new Array();
var redoStack = new Array();

//boolean for toggleing labels
var labelToggle = true;

//the index of the last node the mouse was over
var lastNode = -1;

function reset()
{
	nodeString = new Array();
	nodeText = new Array();
	nodeCount = new Array();
	nodeInformation = new Array();
	nodeId = new Array();
	draw();
}
function init()
{
	/*
	activateAnchor();
	activateImage();
	activeStop();
	 */
}

function stepForward() {
	if(redoStack.length == 0) {
		return;
	}
	
	var nodes = new Array();
	nodes = redoStack.pop();
	
	var nodes_undo = new Array();
	for(var j = 0; j < nodeId.length; j++) {
		nodes_undo[j] = nodeId[j];
	}
	undoStack.push(nodes_undo);
	
	reset();
	count = 0;
	var col = 'blue';
	var termCol = 'red';
	var rad = 10;
	
		for(var z = 0; z < myJSONObject.bindings.length; z++) {
			if(nodes.indexOf(myJSONObject.bindings[z]._wId)!=-1) {
					x = (myJSONObject.bindings[z]._x*XSCALAR)+XSHIFT;
					y = (myJSONObject.bindings[z]._y*YSCALAR)+YSHIFT;
					
					if(labelToggle) {
						name = myJSONObject.bindings[z]._title;
					} else {
						name = "";
					}
					
					if(myJSONObject.bindings[z]._type == "TERMINAL") {
						addNode(x,y,rad,termCol,name);
					} else {
						addNode(x,y,rad,col,name);
					}
					addNodeInformation(count,z);
					count++;
			}
		}
	draw();
}

function stepBack() {	
	if(undoStack.length == 0) {
		return;
	}
	
	var nodes = new Array();
	nodes = undoStack.pop();	
	
	var nodes_redo = new Array();
	for(var j = 0; j < nodeId.length; j++) {
		nodes_redo[j] = nodeId[j];
	}
	redoStack.push(nodes_redo);
	
	reset();
	count = 0;
	var col = 'blue';
	var termCol = 'red';
	var rad = 10;
	
		for(var z = 0; z < myJSONObject.bindings.length; z++) {
			if(nodes.indexOf(myJSONObject.bindings[z]._wId)!=-1) {
					x = (myJSONObject.bindings[z]._x*XSCALAR)+XSHIFT;
					y = (myJSONObject.bindings[z]._y*YSCALAR)+YSHIFT;
					
					if(labelToggle) {
						name = myJSONObject.bindings[z]._title;
					} else {
						name = "";
					}
					
					if(myJSONObject.bindings[z]._type == "TERMINAL") {
						addNode(x,y,rad,termCol,name);
					} else {
						addNode(x,y,rad,col,name);
					}
					addNodeInformation(count,z);
					count++;
			}
		}
	draw();
}

function showLoad()
{
	hideAll();
	document.getElementById('l1').style.visibility="visible";
	document.getElementById('l2').style.visibility="visible";
	clearTimeout(t);
	t=setTimeout("hideAll()",8000);
	hideInformation()
}
function showOptions()
{
	hideAll();
	document.getElementById('o1').style.visibility="visible";
	clearTimeout(t);
	t=setTimeout("hideAll()",8000);
	hideInformation()
}
function showProcess()
{
	hideAll();
	document.getElementById('p1').style.visibility="visible";
	document.getElementById('p2').style.visibility="visible";
	clearTimeout(t);
	t=setTimeout("hideAll()",8000);
	hideInformation()
}
function showSave()
{
	hideAll();
	document.getElementById('s1').style.visibility="visible";
	document.getElementById('s2').style.visibility="visible";
	clearTimeout(t);
	t=setTimeout("hideAll()",8000);
	hideInformation()
}
function hideAll()
{
	//document.getElementById('s1').style.visibility="hidden";
	//document.getElementById('s2').style.visibility="hidden";
	//document.getElementById('p1').style.visibility="hidden";
	//document.getElementById('p2').style.visibility="hidden";
	//document.getElementById('l1').style.visibility="hidden";
	//document.getElementById('l2').style.visibility="hidden";
	//document.getElementById('o1').style.visibility="hidden";
	hideInformation();
	clearTimeout(t);
	clearTimeout(infor);
}

function hideInformation()
{
	document.getElementById('information').style.display='none';

}

function showInformation()
{
	hideAll();
	document.getElementById('information').style.display='block';
	clearTimeout(infor);
	infor=setTimeout("hideInformation()",10000);

}

function toggleLabels() {
	if(labelToggle==true) {
		//Turn off labels
		labelToggle = false;
	} else {
		//Turn on labels
		labelToggle = true;
	}

	count = 0;
	var col = 'blue';
	var termCol = 'red';
	var rad = 10;
	var x,y;
	var name;
	
	var nodes = new Array();
	for(var j = 0; j < nodeId.length; j++) {
		nodes[j] = nodeId[j];
	}
	
	reset();
	
	for(var z = 0; z < myJSONObject.bindings.length; z++) {
		if(nodes.indexOf(myJSONObject.bindings[z]._wId)!=-1) {
			x = (myJSONObject.bindings[z]._x*XSCALAR)+XSHIFT;
			y = (myJSONObject.bindings[z]._y*YSCALAR)+YSHIFT;
			
			if(labelToggle) {
				name = myJSONObject.bindings[z]._title;
			} else {
				name = "";
			}
			if(myJSONObject.bindings[z]._type == "TERMINAL") {
				addNode(x,y,rad,termCol,name);
			} else {
				addNode(x,y,rad,col,name);
			}
			addNodeInformation(count,z);
			count++;
		}
	}
	draw();
}
/*
 * turns on text for the 'nodeNumber'
 * turns off text for all other nodes
 */
function turnOnNodeText(nodeNumber){
	count = 0;
	var col = 'blue';
	var termCol = 'red';
	var rad = 10;
	var x,y;
	var name;
	var dispName;
	
	var nodes = new Array();
	for(var j = 0; j < nodeId.length; j++) {
		nodes[j] = nodeId[j];
	}
	
	dispName = nodeId[nodeNumber];
	
	reset();
	
	for(var z = 0; z < myJSONObject.bindings.length; z++) {
		if(nodes.indexOf(myJSONObject.bindings[z]._wId)!=-1) {
			x = (myJSONObject.bindings[z]._x*XSCALAR)+XSHIFT;
			y = (myJSONObject.bindings[z]._y*YSCALAR)+YSHIFT;
			
			if(myJSONObject.bindings[z]._wId == dispName) {
				name = myJSONObject.bindings[z]._title;
			} else {
				name = "";
			}
			if(myJSONObject.bindings[z]._type == "TERMINAL") {
				addNode(x,y,rad,termCol,name);
			} else {
				addNode(x,y,rad,col,name);
			}
			addNodeInformation(count,z);
			count++;
		}
	}
	draw();
}

function changeNodeInformation(nodeNumber)
{

	
	document.getElementById('information').innerHTML=nodeInformation[nodeNumber];
	showInformation();
	
	if(labelToggle==false) {
		//Turn off labels
		if(lastNode != nodeNumber){
			lastNode = nodeNumber;
			turnOnNodeText(nodeNumber);
		}
	}
	
}

//don't forget to update the nodeInformation in this new node
function addNode(x,y,radius,nodeColor,nodeName)
{

	nodeText[nodeString.length]='<text x="'+(x-4.3*nodeName.length)+'" y="'+(y+2*radius)+'" fill="white">'+nodeName+'</text>';
	nodeString[nodeString.length]='<circle cx="'+x+'" cy="'+y+'" r="'+radius+'" stroke="white" stroke-width="1" fill="'+nodeColor+'" onmouseover="changeNodeInformation('+nodeString.length+')" onClick="nodeClickHandle('+nodeString.length+')" />';
}

function nodeClickHandle(nodeNumber) {
	clickedName = nodeId[nodeNumber];
	
	//Adds the nodes from before the click onto the undo stack
	var nodes = new Array();
	for(var j = 0; j < nodeId.length; j++) {
		nodes[j] = nodeId[j];
	}
	undoStack.push(nodes);
	
	reset();
	count = 0;
	var col = 'blue';
	var termCol = 'red';
	var rad = 10;
	
	for(var i = 0; i < myJSONObject.bindings.length; i++) {

		if(myJSONObject.bindings[i]._wId == clickedName) {
			relationsArray = myJSONObject.bindings[i].relations;
			for(var z = 0; z < myJSONObject.bindings.length; z++) {
				if(relationsArray.indexOf(myJSONObject.bindings[z]._wId)!=-1) {
					x = (myJSONObject.bindings[z]._x*XSCALAR)+XSHIFT;
					y = (myJSONObject.bindings[z]._y*YSCALAR)+YSHIFT;
					
					if(labelToggle) {
						name = myJSONObject.bindings[z]._title;
					} else {
						name = "";
					}
					
					if(myJSONObject.bindings[z]._type == "TERMINAL") {
						addNode(x,y,rad,termCol,name);
					} else {
						addNode(x,y,rad,col,name);
					}
					addNodeInformation(count,z);
					count++;
				}
			}
		}
	}
	draw();

}

function addNodeInformation(infoIndex, jsonIndex){
	infoString = 'Name:<br />' + myJSONObject.bindings[jsonIndex]._title + 
				 '<br /><br />Widget ID:<br />' + myJSONObject.bindings[jsonIndex]._wId + 
				 '<br /><br />Interaction Type:<br />' + myJSONObject.bindings[jsonIndex]._type +
				 '<br /><br />Class:<br />' + myJSONObject.bindings[jsonIndex]._class;
	nodeInformation[infoIndex] = infoString;
	nodeId[infoIndex] = myJSONObject.bindings[jsonIndex]._wId;
}

function addLine(x1,y1,x2,y2,lineColor)
{
	lineString[lineString.length]='<line x1="'+x1+'" y1="'+y1+'" x2="'+x2+'" y2="'+y2+'" style="stroke:'+lineColor+';stroke-width:1"/>';
}

function draw()
{
	document.getElementById('graphContent').innerHTML='<svg id=>'+lineString.join(' ')+nodeString.join(' ')+nodeText.join(' ')+'</svg>';

}

//this is a demo function to show this UI
function testDemo()
{
	//addNode(700,700,15,"#000",'node 111');
	//nodeInformation[0]="This is the information of node 1";
	//addNode(300,200,10,"blue", 'node2222222');
	//nodeInformation[1]="Hey friend, this is the information of node 2";
	//addLine(700,700,300,200,"red");

	var x;
	var y;
	var rad = 10;
	var col = 'blue';
	var termCol = 'red';
	var name;
	var count = 0;

	for(var i = 0; i < myJSONObject.bindings.length; i++) {

		if(myJSONObject.bindings[i]._isRoot == true) {

			x = (myJSONObject.bindings[i]._x*XSCALAR)+XSHIFT;
			y = (myJSONObject.bindings[i]._y*YSCALAR)+YSHIFT;
			name = myJSONObject.bindings[i]._title;

			if(myJSONObject.bindings[i]._type == "TERMINAL") {
				addNode(x,y,rad,termCol,name);
			} else {
				addNode(x,y,rad,col,name);
			}
			addNodeInformation(count,i);
			count++;
		}
	}

	draw();
}