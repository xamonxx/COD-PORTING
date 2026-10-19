#include "runtime.h"

#include <cassert>
#include <string>

int main() {
    vanguard::Runtime runtime("campaign");
    assert(!runtime.initialize("", "cache"));
    assert(runtime.poll_event().find("warning|") == 0);

    assert(runtime.initialize("files", "cache"));
    assert(runtime.poll_event() == "progress|native-contract|1.0");

    assert(!runtime.start("{\"schemaVersion\":1}"));
    assert(runtime.poll_event().find("gameplay core is not integrated") != std::string::npos);

    runtime.stop();
    assert(runtime.poll_event() == "log|info|runtime stopped");
    return 0;
}
