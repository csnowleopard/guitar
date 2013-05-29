//
//  IphoneTimerAppDelegate.h
//  IphoneTimer
//
//  Created by Rongjian Lan on 11/7/11.
//  Copyright 2011 University of Maryland College Park. All rights reserved.
//

#import <UIKit/UIKit.h>

@class IphoneTimerViewController;

@interface IphoneTimerAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    IphoneTimerViewController *viewController;
	BOOL soundAndVibration;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet IphoneTimerViewController *viewController;
@property  BOOL soundAndVibration;
@end

