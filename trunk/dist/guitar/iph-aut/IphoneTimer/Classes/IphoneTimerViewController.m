//
//  IphoneTimerViewController.m
//  IphoneTimer
//
//  Created by Rongjian Lan on 11/7/11.
//  Copyright 2011 University of Maryland College Park. All rights reserved.
//

#import "IphoneTimerViewController.h"
#import "IphoneTimerAppDelegate.h"
#import <AudioToolbox/AudioToolbox.h>

@implementation IphoneTimerViewController

@synthesize currTime;
@synthesize timePicker;
/*
// The designated initializer. Override to perform setup that is required before the view is loaded.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}
*/

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
}
*/



// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
	hours = [[NSArray alloc] initWithObjects:
			 [NSNumber numberWithInt:0],
			 [NSNumber numberWithInt:1],
			 [NSNumber numberWithInt:2],
			 [NSNumber numberWithInt:3],
			 [NSNumber numberWithInt:4],
			 [NSNumber numberWithInt:5],
			 [NSNumber numberWithInt:6],
			 [NSNumber numberWithInt:7],
			 [NSNumber numberWithInt:8],
			 [NSNumber numberWithInt:9],
			 [NSNumber numberWithInt:10],
			 [NSNumber numberWithInt:11],
			 [NSNumber numberWithInt:12],
			 [NSNumber numberWithInt:13],
			 [NSNumber numberWithInt:14],
			 [NSNumber numberWithInt:15],
			 [NSNumber numberWithInt:16],
			 [NSNumber numberWithInt:17],
			 [NSNumber numberWithInt:18],
			 [NSNumber numberWithInt:19],
			 [NSNumber numberWithInt:20],
			 [NSNumber numberWithInt:21],
			 [NSNumber numberWithInt:22],
			 [NSNumber numberWithInt:23],			 
			 nil];
	minutes = [[NSArray alloc] initWithObjects:
			 [NSNumber numberWithInt:0],
			 [NSNumber numberWithInt:5],
			 [NSNumber numberWithInt:10],
			 [NSNumber numberWithInt:15],
			 [NSNumber numberWithInt:20],
			 [NSNumber numberWithInt:25],
			 [NSNumber numberWithInt:30],
			 [NSNumber numberWithInt:35],
			 [NSNumber numberWithInt:40],
			 [NSNumber numberWithInt:45],
			 [NSNumber numberWithInt:50],
			 [NSNumber numberWithInt:55],
			 nil];
	timePicker.dataSource = self;
	timePicker.delegate = self;
	//timePicker = [[UIDatePicker alloc] init];
	//timePicker.datePickerMode = UIDatePickerModeCountDownTimer;
	
	//currTime = [[UILabel alloc] init];
	currTime.text = @"00:00:00";
	dateFormatter = [[NSDateFormatter alloc] init];
	[dateFormatter setDateFormat:@"HH:mm:ss"];
	
	components = [[NSDateComponents alloc] init];
	calendar = [NSCalendar currentCalendar];
	[calendar retain];
	[components setSecond:-1];
	
	NSInvocation *tick = [[NSInvocation alloc] init];
	[tick setSelector:@selector(countDown:)];
	[tick setTarget:self];
	
	[pause retain];
	[started retain];
	[tick release];
    [super viewDidLoad];
	appDelegate = (IphoneTimerAppDelegate *)[[UIApplication sharedApplication] delegate];
}



/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}


- (void)dealloc {
	[currTime release];
	[timePicker release];
	[start release];
	[stop release];
	[reset release];
	[components release];
	[calendar release];
	[counter release];
	[pause release];
	[started release];
    [super dealloc];
}

#pragma mark timingMethods

- (NSInteger) numberOfComponentsInPickerView:(UIPickerView*) pickerView {
	return 2;
}

- (NSInteger) pickerView:(UIPickerView*) pickerView numberOfRowsInComponent:(NSInteger) component {
	if (component == 0) // number of dice
		return [hours count];
	else if (component == 1) // number of sides
		return [minutes count];
	else
		return 0;
}

//- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
//	
//	switch (component) {
//		case 0:
//			return [NSString stringWithFormat:@"%d", row + 1];
//
//		case 1:
//			return nil;
//			
//		default:
//			return nil;
//	}
//	
//}

- (UIView*) pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView*) view {
	
	switch (component) {
		case 0: {
			UILabel* label = [view isKindOfClass:[UILabel class]]? (UILabel*) view : nil;
			if (!label) {
				label = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, 120, 120)] autorelease];
				label.textAlignment = UITextAlignmentCenter;
				label.opaque = NO;
				label.backgroundColor = [UIColor clearColor];
				label.font = [UIFont boldSystemFontOfSize:22];
			}
			
			
			NSNumber *num = [hours objectAtIndex:row];
			label.text = [NSString stringWithFormat:@"%@", num];
			return label;
		}
			
		case 1: {
			UILabel* label = [view isKindOfClass:[UILabel class]]? (UILabel*) view : nil;
			if (!label) {
				label = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, 120, 120)] autorelease];
				label.textAlignment = UITextAlignmentCenter;
				label.opaque = NO;
				label.backgroundColor = [UIColor clearColor];
				label.font = [UIFont boldSystemFontOfSize:22];
			}
			NSNumber *num = [minutes objectAtIndex:row];
			label.text = [NSString stringWithFormat:@"%@", num];
			return label;
		}
			
		default:
			return nil;
	}
}

- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component {
	if (component == 0)
		return 122;
	else
		return 122;
}

- (CGFloat) pickerView:(UIPickerView*) pickerView rowHeightForComponent:(NSInteger) component {
	switch (component) {
		case 0:
			return 44;
			
		case 1:
			return 44;
			
		default:
			return 0;
	}
}

- (void) pickerView:(UIPickerView*) pickerView didSelectRow:(NSInteger) row inComponent:(NSInteger) component {
	if (started == NO) {
		NSCalendar *cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
		NSDateComponents *comps = [cal components:(NSHourCalendarUnit|NSMinuteCalendarUnit|NSSecondCalendarUnit) fromDate:time];
		if (component == 0)
			comps.hour = row;
		else
			comps.minute = [[minutes objectAtIndex:row] integerValue];
		[time release];
		time = [cal dateFromComponents:comps];
		[time retain];
	}
	
}

- (void) playVibration {
	if (!appDelegate.soundAndVibration)
		return;
	NSLog(@"playing sound and vibrating");
	AudioServicesPlayAlertSound(kSystemSoundID_Vibrate); //vibration
	NSLog(@"finished!");
	//if (systemSoundAvailable)
	//	AudioServicesPlayAlertSound(systemSound);
}

- (IBAction) startTimer {
	[self playVibration];
	if (started == NO) {
		//time = [timePicker date];
		currTime.text = [dateFormatter stringFromDate:time];
		if ([currTime.text compare:@"00:00:00"] != NSOrderedSame) {
			[counter release];
			counter = [NSTimer scheduledTimerWithTimeInterval:1.0f target:self selector:@selector(countDown:) userInfo:nil repeats:YES];
			[counter retain];
			started = YES;
		}
	} else {
		if (pause == YES) {
			[counter release];
			counter = [NSTimer scheduledTimerWithTimeInterval:1.0f target:self selector:@selector(countDown:) userInfo:nil repeats:YES];
			[counter retain];
		}
	}
	
}

- (IBAction) stopTimer {
	[self playVibration];
	if (started == YES) {
		pause = YES;
		[counter invalidate];
	}
}

- (IBAction) resetTimer {
	[self playVibration];
	if (pause != NO || started != NO) {
		[time release];
		time = [dateFormatter dateFromString:@"00:00:00"];
		[time retain];
		//[self performSelector:@selector(pickerView:didSelectRow:inComponent:) withObject:timePicker withObject:0 withObject:0];
		//[self performSelector:@selector(pickerView:didSelectRow:inComponent:) withObject:timePicker withObject:0 withObject:1];

		//[timePicker reloadAllComponents];
		[timePicker selectRow:0 inComponent:0 animated:YES];
		[timePicker selectRow:0 inComponent:1 animated:YES];
		[timePicker reloadAllComponents];
		//[UIView commitAnimations];
		currTime.text = [dateFormatter stringFromDate:time];
		[counter invalidate];
		pause = NO;
		started = NO;
	}
}
- (void) countDown:(NSTimer *) theTimer{
	
	[components setSecond:-1];
	time = [calendar dateByAddingComponents:components toDate:time options:0];
	
	[time retain];
	currTime.text = [dateFormatter stringFromDate:time];
	if ([currTime.text compare:@"00:00:00"] == NSOrderedSame) {
		[self playVibration];
		currTime.text = @"Time Is Up!!";
		pause = NO;
		started = NO;
		[counter invalidate];
	}
}
@end
