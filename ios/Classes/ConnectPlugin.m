#import "ConnectPlugin.h"
#if __has_include(<connect/connect-Swift.h>)
#import <connect/connect-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "connect-Swift.h"
#endif

@implementation ConnectPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftConnectPlugin registerWithRegistrar:registrar];
}
@end
