package com.obana.fydmod;

import android.view.KeyEvent;
import android.view.View;

import com.obana.fydmod.*;

public class WifiCarController {
	MainActivity rovio;
	
	public WifiCarController(MainActivity car) {
		rovio = car;
	}

	public void init(View view) {
		if (view != null)  {
			JoystickView joyView = (JoystickView)view;
			joyView.setOnJostickMovedListener(_listener);
		}
	}
	
    public void onKeyDown(int keyCode, KeyEvent event) {
    	int action = event.getAction();
    	if (action == KeyEvent.ACTION_UP) {
    		if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
    			speed++;
    			if (speed > 10) speed = 10;
    		} else if (keyCode == KeyEvent.KEYCODE_BUTTON_L2) {
    			speed--;
    			if (speed < 2) speed = 2;
    		}
    	}
    	
    }
    
    private void backToInit() {

        //wificar.disableMoveFlag();
        try {
        } catch (Exception e3) {
        }
    }

    int speed = 10;
    private void moveToPoint(int x, int y){

    }
    
    private double CalculateAngle(int x, int y) {
        if (x == 0 && y > 0) {
            return 90.0d;
        }
        if (x == 0 && y < 0) {
            return 270.0d;
        }
        double theta = ((Math.atan(y / x) * 360.0d) / 2.0d) / 3.141592653589793d;
        if (x >= 0 && y >= 0) {
            if (theta < 22.5d) {
                return theta + 360.0d;
            }
            return theta;
        } else if (x < 0 && y >= 0) {
            return theta + 180.0d;
        } else {
            if (x < 0 && y < 0) {
                return theta + 180.0d;
            }
            if (x > 0 && y < 0) {
                return theta + 360.0d;
            }
            return theta;
        }
    }

    private double calculateDistance(int x, int y) {
        return Math.sqrt(Math.pow(x, 2.0d) + Math.pow(y, 2.0d));
    }
    
    public enum MOVEMENT_ANGLES {
        FOWARD(112.0d, 68.0d),
        LEFT(202.0d, 158.0d),
        BACKWARD(292.0d, 248.0d),
        RIGHT(382.0d, 338.0d),
        
        LEFT45(157.0d, 113.0d),
        RIGHT45(67.0d, 23.0d),
        LEFT_45(247.0d, 203.0d),
        RIGHT_45(337.0d, 293.0d),;
        
        
        private final double leftAngle;
        private final double rightAngle;

        /* renamed from: values  reason: to resolve conflict with enum method */
        public static MOVEMENT_ANGLES[] valuesCustom() {
            MOVEMENT_ANGLES[] valuesCustom = values();
            int length = valuesCustom.length;
            MOVEMENT_ANGLES[] movement_anglesArr = new MOVEMENT_ANGLES[length];
            System.arraycopy(valuesCustom, 0, movement_anglesArr, 0, length);
            return movement_anglesArr;
        }

        MOVEMENT_ANGLES(double leftAngle, double rightAngle) {
            this.rightAngle = rightAngle;
            this.leftAngle = leftAngle;
        }

        public boolean isInDirection(double angle) {
            return this.leftAngle >= angle && angle >= this.rightAngle;
        }
    }
    
    JoystickView.JoystickMovedListener _listener = new JoystickView.JoystickMovedListener() { // from class: com.uceta.AC13Controller.Controller.7
        @Override // com.uceta.Widgets.JoystickMovedListener
        public void OnMoved(int x, int y) {

        	moveToPoint(x,y);
        }

        @Override // com.uceta.Widgets.JoystickMovedListener
        public void OnReleased() {
        	backToInit();
        }

        @Override // com.uceta.Widgets.JoystickMovedListener
        public void OnReturnedToCenter() {
        	backToInit();
        }
        
        @Override // com.uceta.Widgets.JoystickMovedListener
        public boolean OnKeyDown(int keyCode) {
        	 if (keyCode == 102 ) {
             	//MainActivity.startMoving(1, 5, rovio.f156f);
             	
             } else if (keyCode == 103) {
            	 //MainActivity.startMoving(1, 6, rovio.f156f);
             }
        	 return true;
        }
        
        @Override // com.uceta.Widgets.JoystickMovedListener
        public boolean OnKeyUp(int keyCode) {
        	//MainActivity.stopMoving();
        	 return true;
        }
    };
}
