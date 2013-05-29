//
//  IphoneTimerViewController.h
//  IphoneTimer
//
//  Created by Rongjian Lan on 11/7/11.
//  Copyright 2011 University of Maryland College Park. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "IphoneTimerAppDelegate.h"

@interface IphoneTimerViewController : UIViewController <UIPickerViewDelegate, UIPickerViewDataSource>{
	//IBOutlet UIDatePicker *timePicker;
	IBOutlet UIPickerView *timePicker;
	IBOutlet UIView *container;
	IBOutlet UILabel *currTime;
	
	IBOutlet UIButton *start;
	IBOutlet UIButton *stop;
	IBOutlet UIButton *reset;
	
	IphoneTimerAppDelegate *appDelegate;
	NSDateFormatter *dateFormatter;
	NSDate *time;
	NSTimer *counter;
	NSDateComponents *components;
	NSCalendar *calendar;
	BOOL pause;
	BOOL started;
	
	NSArray* hours;
	NSArray* minutes;
}

- (IBAction) startTimer;
- (IBAction) stopTimer;
- (IBAction) resetTimer;
- (IBAction) pickTime;

@property (nonatomic, retain) UILabel *currTime;
@property (nonatomic, retain) UIPickerView *timePicker;
@end

