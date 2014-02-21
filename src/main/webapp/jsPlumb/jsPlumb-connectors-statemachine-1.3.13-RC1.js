(function(){jsPlumb.Connectors.StateMachine=function(a){var c=null,d,g,k,l,n=[],e=a.curviness||10,u=a.margin||5,B=a.proximityLimit||80,C=a.orientation&&"clockwise"==a.orientation,s=a.loopbackRadius||25,y=!1,D=!1!==a.showLoopback;this.type="StateMachine";a=a||{};this.compute=function(b,a,h,f,p,q,r,t){p=Math.abs(b[0]-a[0]);q=Math.abs(b[1]-a[1]);var m=0.45*p,v=0.45*q;p*=1.9;q*=1.9;r=r||1;var w=Math.min(b[0],a[0])-m,x=Math.min(b[1],a[1])-v;D&&h.elementId==f.elementId?(y=!0,t=b[0],a=b[0],h=b[1]-u,b=b[1]-
u,f=h-s,p=2*r+4*s,q=2*r+4*s,w=t-s-r-s,x=f-s-r-s,c=[w,x,p,q,t-w,f-x,s,C,t-w,h-x,a-w,b-x]):(y=!1,d=b[0]<a[0]?m:p-m,g=b[1]<a[1]?v:q-v,k=b[0]<a[0]?p-m:m,l=b[1]<a[1]?q-v:v,0==b[2]&&(d-=u),1==b[2]&&(d+=u),0==b[3]&&(g-=u),1==b[3]&&(g+=u),0==a[2]&&(k-=u),1==a[2]&&(k+=u),0==a[3]&&(l-=u),1==a[3]&&(l+=u),h=(d+k)/2,f=(g+l)/2,m=-1*h/f,v=Math.atan(m),Infinity==m||-Infinity==m||Math.abs(e/2*Math.sin(v)),Infinity==m||-Infinity==m||Math.abs(e/2*Math.cos(v)),m=d<=k&&l<=g?1:d<=k&&g<=l?2:k<=d&&l>=g?3:4,n=Math.sqrt(Math.pow(k-
d,2)+Math.pow(l-g,2))<=B?[h,f]:1==m?0>=b[3]&&1<=a[3]?[h+(0.5>b[2]?-1*e:e),f]:1<=b[2]&&0>=a[2]?[h,f+(0.5>b[3]?-1*e:e)]:[h+-1*e,f+-1*e]:2==m?1<=b[3]&&0>=a[3]?[h+(0.5>b[2]?-1*e:e),f]:1<=b[2]&&0>=a[2]?[h,f+(0.5>b[3]?-1*e:e)]:[h+1*e,f+-1*e]:3==m?1<=b[3]&&0>=a[3]?[h+(0.5>b[2]?-1*e:e),f]:0>=b[2]&&1<=a[2]?[h,f+(0.5>b[3]?-1*e:e)]:[h+-1*e,f+-1*e]:4==m?0>=b[3]&&1<=a[3]?[h+(0.5>b[2]?-1*e:e),f]:0>=b[2]&&1<=a[2]?[h,f+(0.5>b[3]?-1*e:e)]:[h+1*e,f+-1*e]:void 0,b=Math.max(3*Math.abs(n[0]-d),3*Math.abs(n[0]-k),Math.abs(k-
d),2*r,t),r=Math.max(3*Math.abs(n[1]-g),3*Math.abs(n[1]-l),Math.abs(l-g),2*r,t),p<b&&(t=b-p,w-=t/2,d+=t/2,k+=t/2,p=b,n[0]+=t/2),q<r&&(b=r-q,x-=b/2,g+=b/2,l+=b/2,q=r,n[1]+=b/2),c=[w,x,p,q,d,g,k,l,n[0],n[1]]);return c};var z=function(){return[{x:k,y:l},{x:n[0],y:n[1]},{x:n[0]+1,y:n[1]+1},{x:d,y:g}]},A=function(b,a,c){c&&(a=jsBezier.locationAlongCurveFrom(b,0<a?0:1,a));return a};this.pointOnPath=function(b,a){if(y){a&&(b/=2*Math.PI*s);0<b&&1>b&&(b=1-b);var d=2*b*Math.PI+Math.PI/2,f=c[4]+c[6]*Math.cos(d),
d=c[5]+c[6]*Math.sin(d);return{x:f,y:d}}f=z();b=A(f,b,a);return jsBezier.pointOnCurve(f,b)};this.gradientAtPoint=function(a,c){if(y)return c&&(a/=2*Math.PI*s),Math.atan(2*a*Math.PI);var d=z();a=A(d,a,c);return jsBezier.gradientAtPoint(d,a)};this.pointAlongPathFrom=function(a,d,e){if(y)return e&&(e=2*Math.PI*s,a/=e),0<a&&1>a&&(a=1-a),e=2*Math.PI*c[6],d=2*a*Math.PI-d/e*2*Math.PI+Math.PI/2,a=c[4]+c[6]*Math.cos(d),d=c[5]+c[6]*Math.sin(d),{x:a,y:d};var f=z();a=A(f,a,e);return jsBezier.pointAlongCurveFrom(f,
a,d)}};jsPlumb.Connectors.canvas.StateMachine=function(a){a=a||{};var c=this;jsPlumb.Connectors.StateMachine.apply(this,arguments);jsPlumb.CanvasConnector.apply(this,arguments);this._paint=function(a){10==a.length?(c.ctx.beginPath(),c.ctx.moveTo(a[4],a[5]),c.ctx.bezierCurveTo(a[8],a[9],a[8],a[9],a[6],a[7]),c.ctx.stroke()):(c.ctx.save(),c.ctx.beginPath(),c.ctx.arc(a[4],a[5],a[6],0,2*Math.PI,a[7]),c.ctx.stroke(),c.ctx.closePath(),c.ctx.restore())};this.createGradient=function(a,c){return c.createLinearGradient(a[4],
a[5],a[6],a[7])}};jsPlumb.Connectors.svg.StateMachine=function(){jsPlumb.Connectors.StateMachine.apply(this,arguments);jsPlumb.SvgConnector.apply(this,arguments);this.getPath=function(a){return 10==a.length?"M "+a[4]+" "+a[5]+" C "+a[8]+" "+a[9]+" "+a[8]+" "+a[9]+" "+a[6]+" "+a[7]:"M"+(a[8]+4)+" "+a[9]+" A "+a[6]+" "+a[6]+" 0 1,0 "+(a[8]-4)+" "+a[9]}};jsPlumb.Connectors.vml.StateMachine=function(){jsPlumb.Connectors.StateMachine.apply(this,arguments);jsPlumb.VmlConnector.apply(this,arguments);var a=
jsPlumb.vml.convertValue;this.getPath=function(c){if(10==c.length)return"m"+a(c[4])+","+a(c[5])+" c"+a(c[8])+","+a(c[9])+","+a(c[8])+","+a(c[9])+","+a(c[6])+","+a(c[7])+" e";var d=a(c[8]-c[6]),g=a(c[9]-2*c[6]),k=d+a(2*c[6]),l=g+a(2*c[6]);return"ar "+(d+","+g+","+k+","+l)+","+a(c[8])+","+a(c[9])+","+a(c[8])+","+a(c[9])+" e"}}})();
