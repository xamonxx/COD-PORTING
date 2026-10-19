#include "runtime.h"

#include <utility>

namespace vanguard {

Runtime::Runtime(std::string mode) : mode_(std::move(mode)) {}

bool Runtime::initialize(const std::string& files_dir, const std::string& cache_dir) {
    if (files_dir.empty() || cache_dir.empty()) {
        push("warning|Native runtime received an invalid storage directory");
        return false;
    }
    files_dir_ = files_dir;
    cache_dir_ = cache_dir;
    initialized_ = true;
    push("progress|native-contract|1.0");
    return true;
}

bool Runtime::start(const std::string& request_json) {
    if (!initialized_ || request_json.empty()) {
        push("warning|Native runtime was not initialized with a valid request");
        return false;
    }
    running_ = false;
    push("warning|" + mode_ + " gameplay core is not integrated in this foundation build");
    return false;
}

void Runtime::pause() { push("log|debug|pause requested"); }
void Runtime::resume() { push("log|debug|resume requested"); }

void Runtime::stop() {
    running_ = false;
    push("log|info|runtime stopped");
}

void Runtime::input(int type, int code, float x, float y, float value) {
    (void)type;
    (void)code;
    (void)x;
    (void)y;
    (void)value;
}

std::string Runtime::poll_event() {
    std::lock_guard<std::mutex> lock(mutex_);
    if (events_.empty()) return {};
    std::string event = std::move(events_.front());
    events_.pop();
    return event;
}

void Runtime::push(std::string event) {
    std::lock_guard<std::mutex> lock(mutex_);
    events_.push(std::move(event));
}

}  // namespace vanguard
