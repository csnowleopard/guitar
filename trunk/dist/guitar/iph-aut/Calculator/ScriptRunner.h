//
//  ScriptRunner.h
//  SelfTesting
//
//  Created by Matt Gallagher on 9/10/08.
//  Copyright 2008 Matt Gallagher. All rights reserved.
//
//  Permission is given to use this source code file, free of charge, in any
//  project, commercial or otherwise, entirely at your risk, with the condition
//  that any redistribution (in part or whole) of source code must retain
//  this copyright and permission notice. Attribution in compiled projects is
//  appreciated but not required.
//

#import <UIKit/UIKit.h>

@interface ScriptRunner : NSObject <NSStreamDelegate>
{
	NSMutableArray* guitarCommands;
	NSURLRequest* request;
	NSInputStream *inputStream_;
	NSOutputStream *outputStream_;
	Boolean keep_running;
	Boolean no_SDK;
	id delegate;
	SEL callback;
	SEL errorCallback;
	
	NSUInteger windowNum;
  	NSUInteger numWindows;
	NSArray *buttonsTo;
	NSArray *buttonsFrom;
	
	NSMutableArray *prevGUI;
  	NSUInteger numWindows;
	NSArray *buttonsTo;
	NSArray *buttonsFrom;
}

@property(nonatomic, retain) id delegate;
@property(nonatomic) SEL callback;
@property(nonatomic) SEL errorCallback;

@end
