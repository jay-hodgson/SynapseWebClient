(function(){var q={joinstyle:"stroke-linejoin","stroke-linejoin":"stroke-linejoin","stroke-dashoffset":"stroke-dashoffset","stroke-linecap":"stroke-linecap"},k=function(b,a){for(var c in a)b.setAttribute(c,""+a[c])},g=function(b,a){var c=document.createElementNS("http://www.w3.org/2000/svg",b);a=a||{};a.version="1.1";a.xmlns="http://www.w3.org/1999/xhtml";k(c,a);return c},m=function(b){return"position:absolute;left:"+b[0]+"px;top:"+b[1]+"px"},r=function(b){for(var a=0;a<b.childNodes.length;a++)"linearGradient"!=
b.childNodes[a].tagName&&"radialGradient"!=b.childNodes[a].tagName||b.removeChild(b.childNodes[a])},v=function(b,a,c,d,e){e="jsplumb_gradient_"+e._jsPlumb.idstamp();r(b);var f=c.gradient.offset?g("radialGradient",{id:e}):g("linearGradient",{id:e,gradientUnits:"userSpaceOnUse"});b.appendChild(f);for(b=0;b<c.gradient.stops.length;b++){var h=b,h=8==d.length?d[4]<d[6]?b:c.gradient.stops.length-1-b:d[4]<d[6]?c.gradient.stops.length-1-b:b,h=jsPlumbUtil.convertStyle(c.gradient.stops[h][1],!0),h=g("stop",
{offset:Math.floor(100*c.gradient.stops[b][0])+"%","stop-color":h});f.appendChild(h)}a.setAttribute("style",(c.strokeStyle?"stroke":"fill")+":url(#"+e+")")},l=function(b,a,c,d,e){c.gradient?v(b,a,c,d,e):(r(b),a.setAttribute("style",""));a.setAttribute("fill",c.fillStyle?jsPlumbUtil.convertStyle(c.fillStyle,!0):"none");a.setAttribute("stroke",c.strokeStyle?jsPlumbUtil.convertStyle(c.strokeStyle,!0):"none");c.lineWidth&&a.setAttribute("stroke-width",c.lineWidth);if(c.dashstyle&&c.lineWidth&&!c["stroke-dasharray"]){var f=
-1==c.dashstyle.indexOf(",")?" ":",",h="";c.dashstyle.split(f).forEach(function(a){h+=Math.floor(a*c.lineWidth)+f});a.setAttribute("stroke-dasharray",h)}else c["stroke-dasharray"]&&a.setAttribute("stroke-dasharray",c["stroke-dasharray"]);for(var g in q)c[g]&&a.setAttribute(q[g],c[g])},s=function(b,a,c){c=c.split(" ");for(var d=b.className.baseVal.split(" "),e=0;e<c.length;e++)if(a)-1==d.indexOf(c[e])&&d.push(c[e]);else{var f=d.indexOf(c[e]);-1!=f&&d.splice(f,1)}b.className.baseVal=d.join(" ")};jsPlumbUtil.svg=
{addClass:function(b,a){s(b,!0,a)},removeClass:function(b,a){s(b,!1,a)},node:g,attr:k,pos:m};var t=function(b){var a=this,c=b.pointerEventsSpec||"all";jsPlumb.jsPlumbUIComponent.apply(this,b.originalArgs);a.canvas=null;a.path=null;a.svg=null;var d=b.cssClass+" "+(b.originalArgs[0].cssClass||""),c={style:"",width:0,height:0,"pointer-events":c,position:"absolute"};a.tooltip&&(c.title=a.tooltip);a.svg=g("svg",c);b.useDivWrapper?(a.canvas=document.createElement("div"),a.canvas.style.position="absolute",
jsPlumb.sizeCanvas(a.canvas,0,0,1,1),a.canvas.className=d,a.tooltip&&a.canvas.setAttribute("title",a.tooltip)):(k(a.svg,{"class":d}),a.canvas=a.svg);b._jsPlumb.appendElement(a.canvas,b.originalArgs[0].parent);b.useDivWrapper&&a.canvas.appendChild(a.svg);var e=[a.canvas];this.getDisplayElements=function(){return e};this.appendDisplayElement=function(a){e.push(a)};this.paint=function(c,d,e){if(null!=d){var g=c[0],l=c[1];b.useDivWrapper&&(jsPlumb.sizeCanvas(a.canvas,c[0],c[1],c[2],c[3]),l=g=0);g=m([g,
l,c[2],c[3]]);a.getZIndex()&&(g+=";z-index:"+a.getZIndex()+";");k(a.svg,{style:g,width:c[2],height:c[3]});a._paint.apply(this,arguments)}}},n=jsPlumb.SvgConnector=function(b){var a=this;t.apply(this,[{cssClass:b._jsPlumb.connectorClass,originalArgs:arguments,pointerEventsSpec:"none",tooltip:b.tooltip,_jsPlumb:b._jsPlumb}]);this._paint=function(c,b){var e={d:a.getPath(c)},f=null;e["pointer-events"]="all";if(b.outlineColor){var h=b.lineWidth+2*(b.outlineWidth||1),f=jsPlumb.CurrentLibrary.extend({},
b);f.strokeStyle=jsPlumbUtil.convertStyle(b.outlineColor);f.lineWidth=h;null==a.bgPath?(a.bgPath=g("path",e),a.svg.appendChild(a.bgPath),a.attachListeners(a.bgPath,a)):k(a.bgPath,e);l(a.svg,a.bgPath,f,c,a)}null==a.path?(a.path=g("path",e),a.svg.appendChild(a.path),a.attachListeners(a.path,a)):k(a.path,e);l(a.svg,a.path,b,c,a)};this.reattachListeners=function(){a.bgPath&&a.reattachListenersForElement(a.bgPath,a);a.path&&a.reattachListenersForElement(a.path,a)}};jsPlumb.Connectors.svg.Bezier=function(b){jsPlumb.Connectors.Bezier.apply(this,
arguments);n.apply(this,arguments);this.getPath=function(a){var b="M "+a[4]+" "+a[5];return b+=" C "+a[8]+" "+a[9]+" "+a[10]+" "+a[11]+" "+a[6]+" "+a[7]}};jsPlumb.Connectors.svg.Straight=function(b){jsPlumb.Connectors.Straight.apply(this,arguments);n.apply(this,arguments);this.getPath=function(a){return"M "+a[4]+" "+a[5]+" L "+a[6]+" "+a[7]}};jsPlumb.Connectors.svg.Flowchart=function(){jsPlumb.Connectors.Flowchart.apply(this,arguments);n.apply(this,arguments);this.getPath=function(b){for(var a="M "+
b[4]+","+b[5],c=0;c<b[8];c++)a=a+" L "+b[9+2*c]+" "+b[10+2*c];return a=a+" "+b[6]+","+b[7]}};var u=window.SvgEndpoint=function(b){var a=this;t.apply(this,[{cssClass:b._jsPlumb.endpointClass,originalArgs:arguments,pointerEventsSpec:"all",useDivWrapper:!0,_jsPlumb:b._jsPlumb}]);this._paint=function(b,d){var e=jsPlumb.extend({},d);e.outlineColor&&(e.strokeWidth=e.outlineWidth,e.strokeStyle=jsPlumbUtil.convertStyle(e.outlineColor,!0));null==a.node&&(a.node=a.makeNode(b,e),a.svg.appendChild(a.node),a.attachListeners(a.node,
a));l(a.svg,a.node,e,b,a);m(a.node,b)};this.reattachListeners=function(){a.node&&a.reattachListenersForElement(a.node,a)}};jsPlumb.Endpoints.svg.Dot=function(){jsPlumb.Endpoints.Dot.apply(this,arguments);u.apply(this,arguments);this.makeNode=function(b,a){return g("circle",{cx:b[2]/2,cy:b[3]/2,r:b[2]/2})}};jsPlumb.Endpoints.svg.Rectangle=function(){jsPlumb.Endpoints.Rectangle.apply(this,arguments);u.apply(this,arguments);this.makeNode=function(b,a){return g("rect",{width:b[2],height:b[3]})}};jsPlumb.Endpoints.svg.Image=
jsPlumb.Endpoints.Image;jsPlumb.Endpoints.svg.Blank=jsPlumb.Endpoints.Blank;jsPlumb.Overlays.svg.Label=jsPlumb.Overlays.Label;jsPlumb.Overlays.svg.Custom=jsPlumb.Overlays.Custom;var p=function(b,a){b.apply(this,a);jsPlumb.jsPlumbUIComponent.apply(this,a);this.isAppendedAtTopLevel=!1;var c=this,d=null;this.paint=function(b,f,h,l,m){null==d&&(d=g("path",{"pointer-events":"all"}),b.svg.appendChild(d),c.attachListeners(d,b),c.attachListeners(d,c));k(d,{d:"M"+f.hxy.x+","+f.hxy.y+" L"+f.tail[0].x+","+f.tail[0].y+
" L"+f.cxy.x+","+f.cxy.y+" L"+f.tail[1].x+","+f.tail[1].y+" L"+f.hxy.x+","+f.hxy.y,"class":a&&1==a.length?a[0].cssClass||"":"",stroke:l?l:null,fill:m?m:null})};this.reattachListeners=function(){d&&c.reattachListenersForElement(d,c)};this.cleanup=function(){null!=d&&jsPlumb.CurrentLibrary.removeElement(d)}};jsPlumb.Overlays.svg.Arrow=function(){p.apply(this,[jsPlumb.Overlays.Arrow,arguments])};jsPlumb.Overlays.svg.PlainArrow=function(){p.apply(this,[jsPlumb.Overlays.PlainArrow,arguments])};jsPlumb.Overlays.svg.Diamond=
function(){p.apply(this,[jsPlumb.Overlays.Diamond,arguments])};jsPlumb.Overlays.svg.GuideLines=function(){var b=null,a=this,c,d;jsPlumb.Overlays.GuideLines.apply(this,arguments);this.paint=function(f,h,l,m,n){null==b&&(b=g("path"),f.svg.appendChild(b),a.attachListeners(b,f),a.attachListeners(b,a),c=g("path"),f.svg.appendChild(c),a.attachListeners(c,f),a.attachListeners(c,a),d=g("path"),f.svg.appendChild(d),a.attachListeners(d,f),a.attachListeners(d,a));k(b,{d:e(h[0],h[1]),stroke:"red",fill:null});
k(c,{d:e(h[2][0],h[2][1]),stroke:"blue",fill:null});k(d,{d:e(h[3][0],h[3][1]),stroke:"green",fill:null})};var e=function(a,b){return"M "+a.x+","+a.y+" L"+b.x+","+b.y}}})();
