//
//  HomeViewController.m
//  TabBarAndNav
//
//  Created by Holman on 2/19/12.
//  Copyright 2012 __MyCompanyName__. All rights reserved.
//

#import "HomeViewController.h"


@implementation HomeViewController

@synthesize nameLabel;
@synthesize classLabel;
@synthesize nameField;

- (IBAction)changeGreeting:(id)sender {
	NSString *nameStr = nameField.text;
	if ([nameStr length] == 0) {
		nameStr = @"World";
	}
	NSString *buttonPushed = [sender currentTitle];
	
	NSString *greeting;
	if ([buttonPushed isEqualToString:@"Say Hello"]) {
		greeting = [[NSString alloc] initWithFormat:@"Hello, %@!", nameStr];
	}
	else if ([buttonPushed isEqualToString:@"I'm Grumpy"]) {
		greeting = [[NSString alloc] initWithFormat:@"It sucks to be %@", nameStr];
	}
	nameLabel.text = greeting;
}

- (IBAction)updateClass:(id)sender {
	NSString *buttonPushed = [sender currentTitle];
	
	NSString *greeting;
	if ([buttonPushed isEqualToString:@"CMSC 435"]) {
		greeting = @"That class is so much fun!";
	}
	else if ([buttonPushed isEqualToString:@"CMSC 412"]) {
		greeting = @"Wrong choice.";
	}
	else if ([buttonPushed isEqualToString:@"ENGL 393"]) {
		greeting = @"Really?";
	}	
	else if ([buttonPushed isEqualToString:@"IAML 337"]) {
		greeting = @"Cool story bro.";
	}
	classLabel.text = greeting;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
	if (textField == nameField) {
		[textField resignFirstResponder];
	}
	return YES;
}

// The designated initializer.  Override if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
/*
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization.
    }
    return self;
}
*/

/*
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
}
*/

/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations.
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc. that aren't in use.
}

- (void)viewDidUnload {
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}


- (void)dealloc {
    [super dealloc];
}


@end
