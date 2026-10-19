#pragma once

#include <mutex>
#include <queue>
#include <string>

namespace vanguard {

class Runtime {
   public:
    explicit Runtime(std::string mode);

    bool initialize(const std::string& files_dir, const std::string& cache_dir);
    bool start(const std::string& request_json);
    void pause();
    void resume();
    void stop();
    void input(int type, int code, float x, float y, float value);
    std::string poll_event();

   private:
    void push(std::string event);

    std::string mode_;
    std::string files_dir_;
    std::string cache_dir_;
    bool initialized_ = false;
    bool running_ = false;
    std::mutex mutex_;
    std::queue<std::string> events_;
};

}  // namespace vanguard
